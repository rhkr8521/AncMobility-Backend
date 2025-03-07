package com.rhkr8521.ancmobility.common;

import com.rhkr8521.ancmobility.api.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityMember{
    private Long id;
    private String email;
    private String password;
    private Role role;

}