package ru.vladislemon.yggdrasilserver.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Table("users")
public class User {
    @Id
    private final UUID id;
    private final String email;
    private final String username;
    private final byte[] passwordHash;
    private final byte[] passwordSalt;
    private final boolean emailVerified;
    private final LocalDateTime registeredAt;
    private final LocalDateTime emailVerifiedAt;
    private final LocalDateTime passwordChangedAt;
}
