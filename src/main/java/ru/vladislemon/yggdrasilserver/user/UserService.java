package ru.vladislemon.yggdrasilserver.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.vladislemon.yggdrasilserver.UUIDs;
import ru.vladislemon.yggdrasilserver.validator.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordResetRepository passwordResetRepository;

    private final MailSender mailSender;

    private final EmailValidator emailValidator;
    private final UsernameValidator usernameValidator;
    private final UserCredentialsValidator userCredentialsValidator;
    private final PasswordResetRequestValidator passwordResetRequestValidator;
    private final PasswordChangeRequestValidator passwordChangeRequestValidator;

    private final String host;
    private final String hostname;
    private final int emailVerifyRetryTimeoutSeconds;
    private final int emailVerifyTokenExpireTimeSeconds;
    private final int passwordResetRetryTimeoutSeconds;
    private final int passwordResetTokenExpireTimeSeconds;

    public UserService(
            final UserRepository userRepository,
            final EmailVerificationRepository emailVerificationRepository,
            final PasswordResetRepository passwordResetRepository,
            final MailSender mailSender,
            final EmailValidator emailValidator,
            final UsernameValidator usernameValidator,
            final UserCredentialsValidator userCredentialsValidator,
            final PasswordResetRequestValidator passwordResetRequestValidator,
            final PasswordChangeRequestValidator passwordChangeRequestValidator,
            @Value("${application.host}") final String host,
            @Value("${application.host.name}") final String hostname,
            @Value("${user.email.verification.retry.timeout.seconds}") final int emailVerifyRetryTimeoutSeconds,
            @Value("${user.email.verification.token.expire.seconds}") final int emailVerifyTokenExpireTimeSeconds,
            @Value("${user.password.reset.retry.timeout.seconds}") final int passwordResetRetryTimeoutSeconds,
            @Value("${user.password.reset.token.expire.seconds}") final int passwordResetTokenExpireTimeSeconds
    ) {
        this.userRepository = userRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.mailSender = mailSender;
        this.emailValidator = emailValidator;
        this.usernameValidator = usernameValidator;
        this.userCredentialsValidator = userCredentialsValidator;
        this.passwordResetRequestValidator = passwordResetRequestValidator;
        this.passwordChangeRequestValidator = passwordChangeRequestValidator;
        this.host = host;
        this.hostname = hostname;
        this.emailVerifyRetryTimeoutSeconds = emailVerifyRetryTimeoutSeconds;
        this.emailVerifyTokenExpireTimeSeconds = emailVerifyTokenExpireTimeSeconds;
        this.passwordResetRetryTimeoutSeconds = passwordResetRetryTimeoutSeconds;
        this.passwordResetTokenExpireTimeSeconds = passwordResetTokenExpireTimeSeconds;
    }

    @Transactional
    public User create(final UserCredentials credentials) {
        userCredentialsValidator.validate(credentials);
        final PasswordHashAndSalt hashAndSalt = new PasswordHashAndSalt(credentials.getPassword());
        return userRepository.save(new User(
                null,
                credentials.getEmail(),
                credentials.getUsername(),
                hashAndSalt.getHash(),
                hashAndSalt.getSalt(),
                false,
                LocalDateTime.now(ZoneOffset.UTC),
                null,
                null
        ));
    }

    public Optional<User> findById(final String id) {
        return userRepository.findById(UUIDs.toUUID(id));
    }

    public Optional<User> findByUsername(final String username) {
        usernameValidator.validate(username);
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(final String email) {
        emailValidator.validate(email);
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void sendEmailVerificationMessage(final String email) {
        emailValidator.validate(email);
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        final String verifyEndpoint = "/email/verify";
        final String messageSubject = hostname + " Account registration";
        final String messageTextFormat = "Your activation code: %s\n" +
                                         "You can also click on this link to confirm your email: %s";
        sendVerificationMessage(
                email,
                emailVerificationRepository,
                verifyEndpoint,
                messageSubject,
                messageTextFormat,
                emailVerifyRetryTimeoutSeconds
        );
    }

    @Transactional
    public VerificationToken verifyEmail(final String tokenValue) {
        final VerificationToken token;
        synchronized (this) {
            token = emailVerificationRepository.deleteByTokenValue(UUIDs.toUUID(tokenValue));
        }
        if (isElapsedFromCreation(token, emailVerifyTokenExpireTimeSeconds)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        setVerified(token.getEmail());
        return token;
    }

    @Transactional
    public void setVerified(final String email) {
        emailValidator.validate(email);
        final Optional<User> exist = userRepository.findByEmail(email);
        if (!exist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        final User alreadySavedUser = exist.get();
        if (alreadySavedUser.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        userRepository.save(new User(
                alreadySavedUser.getId(),
                alreadySavedUser.getEmail(),
                alreadySavedUser.getUsername(),
                alreadySavedUser.getPasswordHash(),
                alreadySavedUser.getPasswordSalt(),
                true,
                alreadySavedUser.getRegisteredAt(),
                LocalDateTime.now(ZoneOffset.UTC),
                alreadySavedUser.getPasswordChangedAt()
        ));
    }

    @Transactional
    public void sendPasswordResetMessage(final String email) {
        emailValidator.validate(email);
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        final String verifyEndpoint = "/reset";
        final String messageSubject = hostname + " Password reset";
        final String messageTextFormat = "Your reset code: %s\n" +
                                         "You can also click on this link to reset your password: %s";
        sendVerificationMessage(
                email,
                passwordResetRepository,
                verifyEndpoint,
                messageSubject,
                messageTextFormat,
                passwordResetRetryTimeoutSeconds
        );
    }

    @Transactional
    public VerificationToken resetPassword(final PasswordResetRequest request) {
        passwordResetRequestValidator.validate(request);
        final UUID tokenValue = UUIDs.toUUID(request.getTokenValue());
        final VerificationToken token;
        synchronized (this) {
            token = passwordResetRepository.deleteByTokenValue(tokenValue);
        }
        if (isElapsedFromCreation(token, passwordResetTokenExpireTimeSeconds)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        final Optional<User> user = findByEmail(token.getEmail());
        if (!user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        changePassword(user.get(), request.getNewPassword());
        return token;
    }

    @Transactional
    public void changePassword(final PasswordChangeRequest request) {
        passwordChangeRequestValidator.validate(request);
        final Optional<User> user = userRepository.findById(UUIDs.toUUID(request.getUserId()));
        if (!user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        checkUserPassword(user.get(), request.getPassword());
        changePassword(user.get(), request.getNewPassword());
    }

    private void changePassword(final User user, final String newPassword) {
        final PasswordHashAndSalt hashAndSalt = new PasswordHashAndSalt(newPassword);
        userRepository.save(new User(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                hashAndSalt.getHash(),
                hashAndSalt.getSalt(),
                user.isEmailVerified(),
                user.getRegisteredAt(),
                user.getEmailVerifiedAt(),
                LocalDateTime.now(ZoneOffset.UTC)
        ));
    }

    private void checkUserPassword(final User user, final String password) {
        final PasswordHashAndSalt fromInput = new PasswordHashAndSalt(password, user.getPasswordSalt());
        if (!Arrays.equals(user.getPasswordHash(), fromInput.getHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    private void sendVerificationMessage(
            final String email,
            final VerificationRepository repository,
            final String verifyEndpoint,
            final String messageSubject,
            final String messageTextFormat,
            final int retryTimeoutSeconds
    ) {
        final VerificationToken token;
        synchronized (this) {
            final Optional<VerificationToken> exist = repository.findByEmail(email);
            if (exist.isPresent()) {
                repository.deleteByEmail(email);
                if (!isElapsedFromCreation(exist.get(), retryTimeoutSeconds)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            }
            token = repository.create(
                    new VerificationToken(email, UUID.randomUUID(), LocalDateTime.now(ZoneOffset.UTC))
            );
        }
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(messageSubject);
        final String activationCode = UUIDs.unsign(token.getValue());
        final String link = String.format("%s%s/%s", host, verifyEndpoint, activationCode);
        final String messageText = String.format(messageTextFormat, activationCode, link);
        message.setText(messageText);
        mailSender.send(message);
    }

    private boolean isElapsedFromCreation(final VerificationToken token, final int seconds) {
        return token
                .getCreatedAt()
                .plusSeconds(seconds)
                .isBefore(LocalDateTime.now(ZoneOffset.UTC));
    }
}
