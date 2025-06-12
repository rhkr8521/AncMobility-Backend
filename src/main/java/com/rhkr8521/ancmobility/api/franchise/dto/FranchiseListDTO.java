package com.rhkr8521.ancmobility.api.franchise.dto;

import com.rhkr8521.ancmobility.api.franchise.entity.Franchise;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FranchiseListDTO {

    private Long id;
    private String name;
    private String carNumber;
    private String phoneNumber;

    public FranchiseListDTO(Franchise franchise) {
        this.id = franchise.getId();
        this.name = franchise.getName();
        this.carNumber = franchise.getCarNumber();
        this.phoneNumber = franchise.getPhoneNumber();
    }

    public static FranchiseListDTO from(Franchise franchise) {
        return new FranchiseListDTO(franchise);
    }
}
