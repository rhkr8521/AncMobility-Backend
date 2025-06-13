package com.rhkr8521.ancmobility.api.contact.controller;

import com.rhkr8521.ancmobility.api.contact.dto.ContactCreateDTO;
import com.rhkr8521.ancmobility.api.contact.dto.ContactResponseDTO;
import com.rhkr8521.ancmobility.api.contact.service.ContactService;
import com.rhkr8521.ancmobility.api.home.dto.HomeRequestDTO;
import com.rhkr8521.ancmobility.api.home.dto.HomeResponseDTO;
import com.rhkr8521.ancmobility.common.SecurityMember;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Contact", description = "문의처 정보 관련 API")
@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @Operation(summary = "문의처 정보 등록")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> createContactInfo(
            @RequestParam("address") String address,
            @RequestParam("telephone") String telephone,
            @RequestParam("fax") String fax,
            @RequestParam("email") String email,
            @RequestParam(value = "addressImage", required = false) MultipartFile addressImage,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        ContactCreateDTO contactCreateDTO = ContactCreateDTO.builder()
                .address(address)
                .telephone(telephone)
                .fax(fax)
                .email(email)
                .build();

        contactService.createContactInfo(contactCreateDTO, addressImage, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.SAVE_CONTACT_SUCCESS);
    }

    @Operation(summary = "문의처 정보 수정")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateContactInfo(
            @RequestParam("address") String address,
            @RequestParam("telephone") String telephone,
            @RequestParam("fax") String fax,
            @RequestParam("email") String email,
            @RequestParam(value = "addressImage", required = false) MultipartFile addressImage,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        ContactCreateDTO contactCreateDTO = ContactCreateDTO.builder()
                .address(address)
                .telephone(telephone)
                .fax(fax)
                .email(email)
                .build();

        contactService.updateContactInfo(contactCreateDTO, addressImage, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.UPDATE_CONTACT_SUCCESS);
    }

    @Operation(summary = "문의처 정보 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<ContactResponseDTO>> getContactInfo(
    ) {
        ContactResponseDTO contactResponseDTO = contactService.getContactInfo();
        return ApiResponse.success(SuccessStatus.GET_CONTACT_SUCCESS, contactResponseDTO);
    }
}
