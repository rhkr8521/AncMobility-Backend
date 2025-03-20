package com.rhkr8521.ancmobility.api.companyinfo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
@Table(name = "CompanyInfo")
@AllArgsConstructor
public class CompanyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subTitle;
    private String bannerImage;
    private String image;

    @Enumerated(EnumType.STRING)
    private CompanyInfoType companyInfoType;
}
