package com.rhkr8521.ancmobility.api.notice.entity;

import com.rhkr8521.ancmobility.api.member.entity.Member;
import com.rhkr8521.ancmobility.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
@Table(name = "Notice")
@AllArgsConstructor
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private long viewCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;
}
