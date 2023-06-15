package ru.vladislemon.yggdrasilserver.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class VerificationRepository {
    private final Map<String, VerificationToken> emailToToken = new HashMap<>();
    private final Map<UUID, String> tokenValueToEmail = new HashMap<>();

    VerificationToken create(final VerificationToken token) {
        emailToToken.put(token.getEmail(), token);
        tokenValueToEmail.put(token.getValue(), token.getEmail());
        return token;
    }

    Optional<VerificationToken> findByEmail(final String email) {
        return Optional.ofNullable(emailToToken.get(email));
    }

    Optional<VerificationToken> findByTokenValue(final UUID tokenValue) {
        if (tokenValueToEmail.containsKey(tokenValue)) {
            return Optional.ofNullable(emailToToken.get(tokenValueToEmail.get(tokenValue)));
        }
        return Optional.empty();
    }

    VerificationToken deleteByEmail(final String email) {
        final VerificationToken token = emailToToken.get(email);
        tokenValueToEmail.remove(token.getValue());
        emailToToken.remove(email);
        return token;
    }

    VerificationToken deleteByTokenValue(final UUID tokenValue) {
        if (!tokenValueToEmail.containsKey(tokenValue)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        final String email = tokenValueToEmail.get(tokenValue);
        final VerificationToken token = emailToToken.get(email);
        tokenValueToEmail.remove(tokenValue);
        emailToToken.remove(email);
        return token;
    }
}
