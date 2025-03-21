package com.rhkr8521.ancmobility.api.home.dto;

import com.rhkr8521.ancmobility.api.home.entity.Home;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeResponseDTO {

    private String title;
    private String subTitle;
    private String bannerImage1;
    private String bannerImage2;
    private String bannerImage3;

    public static HomeResponseDTO fromEntity(Home home) {
        return HomeResponseDTO.builder()
                .title(home.getTitle())
                .subTitle(home.getSubTitle())
                .bannerImage1(home.getBannerImage1())
                .bannerImage2(home.getBannerImage2())
                .bannerImage3(home.getBannerImage3())
                .build();
    }

}
