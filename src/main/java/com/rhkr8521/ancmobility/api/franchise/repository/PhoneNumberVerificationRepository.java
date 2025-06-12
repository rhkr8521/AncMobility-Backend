package com.rhkr8521.ancmobility.api.franchise.repository;

import com.rhkr8521.ancmobility.api.franchise.entity.PhoneNumberVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneNumberVerificationRepository extends JpaRepository<PhoneNumberVerification, Long> {
    Optional<PhoneNumberVerification> findByPhoneNumber(String phoneNumber);

    Optional<PhoneNumberVerification> findByCode(String code);
}
