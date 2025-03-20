package com.rhkr8521.ancmobility.api.serviceinfo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
@Table(name = "ServiceInfo")
@AllArgsConstructor
public class ServiceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subTitle;
    private String bannerImage;
    private String image;
    private String apply;

    @Enumerated(EnumType.STRING)
    private ServiceInfoType serviceInfoType;
}
