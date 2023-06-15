package ru.vladislemon.yggdrasilserver.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class VerificationToken {
    private final String email;
    private final UUID value;
    private final LocalDateTime createdAt;
}
