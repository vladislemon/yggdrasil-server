package ru.vladislemon.yggdrasilserver;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public final class UUIDs {
    private UUIDs() {
    }

    public static String unsign(final UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static UUID toUUID(final String uuid) {
        switch (uuid.length()) {
            case 36:
                return UUID.fromString(uuid);

            case 32:
                return UUID.fromString(uuid.substring(0, 8) +
                                       "-" +
                                       uuid.substring(8, 12) +
                                       "-" +
                                       uuid.substring(12, 16) +
                                       "-" +
                                       uuid.substring(16, 20) +
                                       "-" +
                                       uuid.substring(20, 32));

            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }
    }
}
