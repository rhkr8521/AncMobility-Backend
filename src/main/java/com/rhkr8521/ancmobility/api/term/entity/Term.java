package com.rhkr8521.ancmobility.api.term.entity;

import com.rhkr8521.ancmobility.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
@Table(name = "TERM")
@AllArgsConstructor
public class Term extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private TermType termType; // 글 타입
}
