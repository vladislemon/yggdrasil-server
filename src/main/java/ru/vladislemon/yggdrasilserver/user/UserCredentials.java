package ru.vladislemon.yggdrasilserver.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCredentials {
    private final String email;
    private final String username;
    private final String password;
}
