package ru.vladislemon.yggdrasilserver.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailValidator extends StringPatternValidator {
    protected EmailValidator() {
        super(Pattern.compile("^[A-Z0-9._%+-]{1,64}@[A-Z0-9.-]{1,255}\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE));
    }

    @Override
    protected String getExceptionReason() {
        return "Invalid email";
    }
}
