package com.rhkr8521.ancmobility.api.companyinfo.dto;

import com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInfoRequestDTO {
    private CompanyInfoType companyInfoType;
    private String title;
    private String subTitle;
    private MultipartFile image;
}