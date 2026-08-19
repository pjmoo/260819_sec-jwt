package org.example.secjwt.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAccountService {
    private final UserAccountJpaRepository userAccountJpaRepository;
    private final PasswordEncoder passwordEncoder; // SecurityConfig

    @Transactional
    public UserAccountEntity signUp(UserAccountEntity entity) {
//        return userAccountJpaRepository.save(entity); // password 문제가 있음
        // 중복가입 문제를 배제하기 위해
        if (userAccountJpaRepository.findByUsername(entity.getUsername())
                .isPresent()) {
            throw new IllegalArgumentException("이미 가입된 사용자");
        }
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        return userAccountJpaRepository.save(entity);
    }
}
