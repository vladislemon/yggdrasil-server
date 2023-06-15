package ru.vladislemon.yggdrasilserver.validator;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.vladislemon.yggdrasilserver.profile.ProfileCredentials;

@Component
@AllArgsConstructor
public class ProfileCredentialsValidator extends ObjectValidator<ProfileCredentials> {
    private final UserIdValidator userIdValidator;
    private final UsernameValidator usernameValidator;

    @Override
    public void validate(final ProfileCredentials credentials) {
        checkArgument(credentials != null, new ResponseStatusException(HttpStatus.BAD_REQUEST));
        userIdValidator.validate(credentials.getUserId());
        usernameValidator.validate(credentials.getName());
    }
}
