package com.rhkr8521.ancmobility.api.member.dto;

import com.rhkr8521.ancmobility.api.member.entity.Member;
import com.rhkr8521.ancmobility.api.member.entity.Role;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class UserInfoResponseDTO {

    private String nickname;
    private String email;
    private Role role;

    public UserInfoResponseDTO(Member member) {
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.role = member.getRole();
    }
}
