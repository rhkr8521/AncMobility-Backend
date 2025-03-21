package com.rhkr8521.ancmobility.api.home.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
@Table(name = "Home")
@AllArgsConstructor
public class Home {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subTitle;
    private String bannerImage1;
    private String bannerImage2;
    private String bannerImage3;
}
