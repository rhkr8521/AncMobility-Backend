package com.rhkr8521.ancmobility.api.companyinfo.dto;

import com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfo;
import com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfoType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyInfoResponseDTO {
    private CompanyInfoType companyInfoType;
    private String title;
    private String subTitle;
    private String bannerImage;
    private String image;

    public static CompanyInfoResponseDTO fromEntity(CompanyInfo companyInfo) {
        return CompanyInfoResponseDTO.builder()
                .companyInfoType(companyInfo.getCompanyInfoType())
                .title(companyInfo.getTitle())
                .subTitle(companyInfo.getSubTitle())
                .bannerImage(companyInfo.getBannerImage())
                .image(companyInfo.getImage())
                .build();
    }
}