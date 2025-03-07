package com.rhkr8521.ancmobility.api.member.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class MemberRegisterRequestDTO {

    private String userId;
    private String username;
    private String password;
    private String email;
}
