package com.rhkr8521.ancmobility.api.franchise.dto;

import com.rhkr8521.ancmobility.api.franchise.entity.Franchise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranchiseCreateRequestDTO {
    private String name;
    private String phoneNumber;
    private String carNumber;
}