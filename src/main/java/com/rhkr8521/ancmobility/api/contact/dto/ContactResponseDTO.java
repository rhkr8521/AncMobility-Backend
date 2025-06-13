package com.rhkr8521.ancmobility.api.contact.dto;

import com.rhkr8521.ancmobility.api.contact.entity.Contact;
import com.rhkr8521.ancmobility.api.home.dto.HomeResponseDTO;
import com.rhkr8521.ancmobility.api.home.entity.Home;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContactResponseDTO {

    private String address;
    private String addressImage;
    private String telephone;
    private String fax;
    private String email;

    public static ContactResponseDTO fromEntity(Contact contact) {
        return ContactResponseDTO.builder()
                .address(contact.getAddress())
                .addressImage(contact.getAddressImage())
                .telephone(contact.getTelephone())
                .fax(contact.getFax())
                .email(contact.getEmail())
                .build();
    }

}
