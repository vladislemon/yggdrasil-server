package ru.vladislemon.yggdrasilserver.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TokenValueValidator extends ObjectValidator<String> {
    @Override
    public void validate(final String tokenValue) {
        checkArgument(tokenValue != null, new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
}
