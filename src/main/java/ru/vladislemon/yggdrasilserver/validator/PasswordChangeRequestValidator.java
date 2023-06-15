package ru.vladislemon.yggdrasilserver.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vladislemon.yggdrasilserver.user.PasswordChangeRequest;

@Component
@AllArgsConstructor
public class PasswordChangeRequestValidator extends ObjectValidator<PasswordChangeRequest> {
    private final UserIdValidator userIdValidator;
    private final PasswordValidator passwordValidator;

    @Override
    public void validate(final PasswordChangeRequest request) {
        checkArgument(request != null);
        userIdValidator.validate(request.getUserId());
        passwordValidator.validate(request.getPassword());
        passwordValidator.validate(request.getNewPassword());
    }
}
