package ru.vladislemon.yggdrasilserver.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {
    private final String id;
    private final String username;
}
