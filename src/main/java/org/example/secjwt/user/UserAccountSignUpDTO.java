package org.example.secjwt.user;

import jakarta.validation.constraints.NotBlank;

public record UserAccountSignUpDTO(
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotBlank
        String nickname
) {
    public UserAccountEntity toEntity() {
        return UserAccountEntity.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
