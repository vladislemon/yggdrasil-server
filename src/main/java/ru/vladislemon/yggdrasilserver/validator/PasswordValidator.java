package ru.vladislemon.yggdrasilserver.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator extends StringPatternValidator {
    protected PasswordValidator() {
        super(Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,64}$"));
    }

    @Override
    protected String getExceptionReason() {
        return "Invalid password";
    }
}
