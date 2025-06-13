package com.rhkr8521.ancmobility.api.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactCreateDTO {

    private String address;
    private String addressImage;
    private String telephone;
    private String fax;
    private String email;
}
