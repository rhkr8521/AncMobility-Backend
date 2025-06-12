package com.rhkr8521.ancmobility.api.franchise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PhoneAuthResponseDTO {

    private String name;
    private String phoneNumber;
    private String carNumber;
    private String token;
}
