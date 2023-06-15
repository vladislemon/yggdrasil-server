package ru.vladislemon.yggdrasilserver.validator;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.vladislemon.yggdrasilserver.user.UserCredentials;

@Component
@AllArgsConstructor
public class UserCredentialsValidator extends ObjectValidator<UserCredentials> {
    private final EmailValidator emailValidator;
    private final UsernameValidator usernameValidator;
    private final PasswordValidator passwordValidator;

    @Override
    public void validate(final UserCredentials credentials) {
        checkArgument(credentials != null, new ResponseStatusException(HttpStatus.BAD_REQUEST));
        emailValidator.validate(credentials.getEmail());
        usernameValidator.validate(credentials.getUsername());
        passwordValidator.validate(credentials.getPassword());
    }
}
