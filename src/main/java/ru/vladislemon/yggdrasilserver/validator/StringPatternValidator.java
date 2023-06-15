package ru.vladislemon.yggdrasilserver.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

public abstract class StringPatternValidator extends ObjectValidator<String> {
    private final Pattern pattern;

    protected StringPatternValidator(final Pattern pattern) {
        this.pattern = pattern;
    }

    protected String getExceptionReason() {
        return null;
    }

    @Override
    public final void validate(final String object) {
        checkArgument(object != null,
                new ResponseStatusException(HttpStatus.BAD_REQUEST, getExceptionReason()));
        checkArgument(pattern.matcher(object).matches(),
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, getExceptionReason()));
    }
}
