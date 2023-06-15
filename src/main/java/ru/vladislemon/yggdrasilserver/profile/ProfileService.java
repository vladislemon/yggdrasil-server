package ru.vladislemon.yggdrasilserver.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.vladislemon.yggdrasilserver.UUIDs;
import ru.vladislemon.yggdrasilserver.validator.ProfileCredentialsValidator;
import ru.vladislemon.yggdrasilserver.user.User;
import ru.vladislemon.yggdrasilserver.user.UserService;
import ru.vladislemon.yggdrasilserver.validator.UsernameValidator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {
    private final int maxProfilesPerUser;
    private final ProfileRepository profileRepository;
    private final ProfileCredentialsValidator profileCredentialsValidator;
    private final UserService userService;
    private final UsernameValidator usernameValidator;

    public ProfileService(
            @Value("${profile.per_user.max}") final int maxProfilesPerUser,
            final ProfileRepository profileRepository,
            final ProfileCredentialsValidator profileCredentialsValidator,
            final UserService userService,
            final UsernameValidator usernameValidator
    ) {
        this.maxProfilesPerUser = maxProfilesPerUser;
        this.profileRepository = profileRepository;
        this.profileCredentialsValidator = profileCredentialsValidator;
        this.userService = userService;
        this.usernameValidator = usernameValidator;
    }

    @Transactional
    public Profile create(final ProfileCredentials credentials) {
        profileCredentialsValidator.validate(credentials);
        final Optional<User> user = userService.findById(credentials.getUserId());
        if (!user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        final UUID userId = UUIDs.toUUID(credentials.getUserId());
        if (profileRepository.countByUserId(userId) >= maxProfilesPerUser) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return profileRepository.save(new Profile(
                null,
                userId,
                credentials.getName(),
                LocalDateTime.now(ZoneOffset.UTC),
                null
        ));
    }

    public Iterable<Profile> findByUserId(final String userId) {
        return profileRepository.findByUserId(UUIDs.toUUID(userId));
    }

    public Optional<Profile> findByName(final String name) {
        usernameValidator.validate(name);
        return profileRepository.findByName(name);
    }
}
