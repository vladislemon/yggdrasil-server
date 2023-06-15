package ru.vladislemon.yggdrasilserver.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserIdValidator extends ObjectValidator<String> {
    @Override
    public void validate(final String userId) {
        checkArgument(userId != null, new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
}
