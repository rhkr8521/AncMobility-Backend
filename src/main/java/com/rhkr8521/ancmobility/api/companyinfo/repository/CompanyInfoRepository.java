package com.rhkr8521.ancmobility.api.companyinfo.repository;

import com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfo;
import com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfoType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {
    Optional<CompanyInfo> findByCompanyInfoType(CompanyInfoType companyInfoType);
}