package ru.vladislemon.yggdrasilserver.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileCredentials {
    private final String userId;
    private final String name;
}
