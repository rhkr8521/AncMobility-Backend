package com.rhkr8521.ancmobility.api.member.entity;

import com.rhkr8521.ancmobility.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
@Table(name = "MEMBER")
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // 사용자 ID
    private String email; // 이메일
    private String nickname; // 닉네임
    private String password; // 비밀번호
    private String refreshToken; // 리프레시 토큰

    @Enumerated(EnumType.STRING)
    private Role role; // 권한


    // 리프레시토큰 업데이트
    public Member updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
