package ru.vladislemon.yggdrasilserver.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UsernameValidator extends StringPatternValidator {
    protected UsernameValidator() {
        super(Pattern.compile("^[a-zA-Z0-9._-]{3,32}$"));
    }

    @Override
    protected String getExceptionReason() {
        return "Invalid username";
    }
}
