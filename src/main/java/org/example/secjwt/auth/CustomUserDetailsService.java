package org.example.secjwt.auth;

import lombok.RequiredArgsConstructor;
import org.example.secjwt.user.UserAccountEntity;
import org.example.secjwt.user.UserAccountJpaRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// UsernamePasswordAuthenticationFilter
public class CustomUserDetailsService implements UserDetailsService {
    private final UserAccountJpaRepository userAccountJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("서비스 진입");
        // username에 대응하는 entity가 있고 그걸 UserDetail로 변환해서 반환만
        // 검증은 이미 완료했고, 그 검증을 마친 걸 jwt가 보여줌
        // -> 가끔씩 탈퇴나 기타 문제로 인해서 user가 없어지는 경우가 생김
        // jwt 인증 시점과 jwt filter에서 jwt를 사용하는 시점 간 차이로 인해...
        UserAccountEntity entity = userAccountJpaRepository.findByUsername(username).orElseThrow();
        // 예외 뜨면 뒤로 진행 X
        System.out.println("Details 생성");
        return CustomUserDetails.builder()
                .username(entity.getUsername())
//                .password(entity.getPassword()) // 보안상 없는 걸 추천
                .authorities(AuthorityUtils.createAuthorityList("ROLE_USER"))
                .build();
    }
}
