package org.example.secjwt.user;

import lombok.RequiredArgsConstructor;
import org.example.secjwt.auth.JwtProvider;
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

    private final JwtProvider jwtProvider;

    public String login(String username,
                        String password) {
        // 존재여부 확인
        UserAccountEntity entity = userAccountJpaRepository.findByUsername(username)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자")
                );
        // 해당 username 없으면 에러처리 -> 나중에 ExceptionHandler로 받아서
        // '없어서 에러가 난다'는 사실을 알려주면 안 됩니다
        // 패스워드 비교
        if (!passwordEncoder.matches(password, entity.getPassword())) {
            throw new IllegalArgumentException("일치하지 않는 비밀번호");
        }
        // 두 에러를 구분해서 알려주지 않음
        return jwtProvider.issueAccessToken(username);
    }
}
