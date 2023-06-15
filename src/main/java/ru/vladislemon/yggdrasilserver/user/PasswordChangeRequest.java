package ru.vladislemon.yggdrasilserver.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordChangeRequest {
    private final String userId;
    private final String password;
    private final String newPassword;
}
