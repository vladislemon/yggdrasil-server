package ru.vladislemon.yggdrasilserver.validator;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.vladislemon.yggdrasilserver.user.PasswordResetRequest;

@Component
@AllArgsConstructor
public class PasswordResetRequestValidator extends ObjectValidator<PasswordResetRequest> {
    private final TokenValueValidator tokenValueValidator;
    private final PasswordValidator passwordValidator;

    @Override
    public void validate(final PasswordResetRequest request) {
        checkArgument(request != null, new ResponseStatusException(HttpStatus.BAD_REQUEST));
        tokenValueValidator.validate(request.getTokenValue());
        passwordValidator.validate(request.getNewPassword());
    }
}
