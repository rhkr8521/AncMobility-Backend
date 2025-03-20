package com.rhkr8521.ancmobility.api.serviceinfo.repository;

import com.rhkr8521.ancmobility.api.serviceinfo.entity.ServiceInfo;
import com.rhkr8521.ancmobility.api.serviceinfo.entity.ServiceInfoType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceInfoRepository extends JpaRepository<ServiceInfo, Long> {
    Optional<ServiceInfo> findByServiceInfoType(ServiceInfoType serviceInfoType);
}