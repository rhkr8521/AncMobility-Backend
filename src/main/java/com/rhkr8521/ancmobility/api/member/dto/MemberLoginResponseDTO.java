package com.rhkr8521.ancmobility.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String role;
}