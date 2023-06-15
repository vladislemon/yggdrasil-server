package ru.vladislemon.yggdrasilserver.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Table("profiles")
public class Profile {
    @Id
    private final UUID id;
    private final UUID userId;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime nameChangedAt;
}
