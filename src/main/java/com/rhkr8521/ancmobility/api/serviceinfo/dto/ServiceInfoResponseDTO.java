package com.rhkr8521.ancmobility.api.serviceinfo.dto;

import com.rhkr8521.ancmobility.api.serviceinfo.entity.ServiceInfo;
import com.rhkr8521.ancmobility.api.serviceinfo.entity.ServiceInfoType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceInfoResponseDTO {
    private ServiceInfoType serviceInfoType;
    private String title;
    private String subTitle;
    private String bannerImage;
    private String image;
    private String apply;

    public static ServiceInfoResponseDTO fromEntity(ServiceInfo serviceInfo) {
        return ServiceInfoResponseDTO.builder()
                .serviceInfoType(serviceInfo.getServiceInfoType())
                .title(serviceInfo.getTitle())
                .subTitle(serviceInfo.getSubTitle())
                .bannerImage(serviceInfo.getBannerImage())
                .image(serviceInfo.getImage())
                .apply(serviceInfo.getApply())
                .build();
    }
}