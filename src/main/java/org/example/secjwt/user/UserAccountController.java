package org.example.secjwt.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserAccountController {
    private final UserAccountService userAccountService; // @RequiredArgsConstructor

    @PostMapping("/signup")
    public ResponseEntity<UserAccountEntity> signUp(
            @Validated @RequestBody UserAccountSignUpDTO dto) {
        UserAccountEntity saved = userAccountService.signUp(dto.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }
}
