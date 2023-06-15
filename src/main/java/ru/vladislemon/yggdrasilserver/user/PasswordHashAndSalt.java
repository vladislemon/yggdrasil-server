package ru.vladislemon.yggdrasilserver.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Getter
public class PasswordHashAndSalt {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 64;
    private static final MessageDigest SHA_512;

    static {
        try {
            SHA_512 = MessageDigest.getInstance("SHA-512");
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private final byte[] hash;
    private final byte[] salt;

    public PasswordHashAndSalt(final String password) {
        this(password, generateSalt());
    }

    public PasswordHashAndSalt(final String password, final byte[] salt) {
        if (salt.length != SALT_LENGTH) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        this.salt = salt;
        final byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        final byte[] saltPasswordBytes = new byte[passwordBytes.length + salt.length];
        System.arraycopy(passwordBytes, 0, saltPasswordBytes, 0, passwordBytes.length);
        System.arraycopy(salt, 0, saltPasswordBytes, passwordBytes.length, salt.length);
        this.hash = SHA_512.digest(saltPasswordBytes);
    }

    private static byte[] generateSalt() {
        final byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return salt;
    }
}
