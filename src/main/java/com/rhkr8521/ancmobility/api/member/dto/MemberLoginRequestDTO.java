package com.rhkr8521.ancmobility.api.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginRequestDTO {
    private String userId;
    private String password;

    public MemberLoginRequestDTO(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}