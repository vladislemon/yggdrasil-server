package ru.vladislemon.yggdrasilserver.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.vladislemon.yggdrasilserver.UUIDs;

@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @Transactional
    public ResponseEntity<UserInfo> create(@RequestBody final UserCredentials credentials) {
        final User user = userService.create(credentials);
        userService.sendEmailVerificationMessage(user.getEmail());
        return ResponseEntity.ok(new UserInfo(
                UUIDs.unsign(user.getId()),
                user.getUsername()
        ));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserInfo> findById(@PathVariable("id") final String id) {
        return ResponseEntity.of(userService.findById(id).map(user -> new UserInfo(
                UUIDs.unsign(user.getId()),
                user.getUsername()
        )));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserInfo> findByUsername(@PathVariable("username") final String username) {
        return ResponseEntity.of(userService.findByUsername(username).map(user -> new UserInfo(
                UUIDs.unsign(user.getId()),
                user.getUsername()
        )));
    }

    @PostMapping("email/verify")
    public HttpStatus sendEmailVerificationMessage(@RequestParam("email") final String email) {
        userService.sendEmailVerificationMessage(email);
        return HttpStatus.OK;
    }

    @GetMapping("email/verify/{tokenValue}")
    public ResponseEntity<VerificationToken> verifyEmail(@PathVariable("tokenValue") final String tokenValue) {
        return ResponseEntity.ok(userService.verifyEmail(tokenValue));
    }

    @PostMapping("password/reset")
    public HttpStatus sendPasswordResetMessage(@RequestParam("email") final String email) {
        if (!userService.findByEmail(email).isPresent()) {
            return HttpStatus.NOT_FOUND;
        }
        userService.sendPasswordResetMessage(email);
        return HttpStatus.OK;
    }

    @PostMapping("password/reset/{tokenValue}")
    public ResponseEntity<VerificationToken> resetPassword(
            @PathVariable("tokenValue") final String tokenValue,
            @RequestParam("newPassword") final String newPassword
    ) {
        return ResponseEntity.ok(userService.resetPassword(new PasswordResetRequest(
                tokenValue,
                newPassword
        )));
    }
}
