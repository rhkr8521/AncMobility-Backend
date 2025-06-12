package com.rhkr8521.ancmobility.common;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityFranchiseMember {
    private Long id;
    private String name;
    private String carNumber;
    private String phoneNumber;
}
