package org.example.secjwt.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.example.secjwt.common.BaseEntity;

@Table(name = "user_account")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountEntity extends BaseEntity {
    private String username; // 로그인 시 쓰는 이름
    private String nickname; // 표시하는 이름
    @Setter
    private String password;
}
