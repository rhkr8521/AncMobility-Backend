package com.rhkr8521.ancmobility.api.franchise.entity;

import com.rhkr8521.ancmobility.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PhoneNumberVerification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String code;
    private Integer expirationTimeInMinutes;

    @Builder.Default
    private boolean isVerified = false;

    public boolean isExpired(LocalDateTime verifiedAt) {
        return verifiedAt.isAfter(this.createdAt.plusMinutes(this.expirationTimeInMinutes));
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
}