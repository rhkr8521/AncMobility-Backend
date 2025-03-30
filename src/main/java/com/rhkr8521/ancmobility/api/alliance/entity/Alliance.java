package com.rhkr8521.ancmobility.api.alliance.entity;

import com.rhkr8521.ancmobility.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
@Table(name = "Alliance")
@AllArgsConstructor
public class Alliance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subTitle;
    private String company;
    private String image;
    private Boolean active;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = Tag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "alliance_tags", joinColumns = @JoinColumn(name = "alliance_id"))
    @Column(name = "tag")
    private List<Tag> tags;
}
