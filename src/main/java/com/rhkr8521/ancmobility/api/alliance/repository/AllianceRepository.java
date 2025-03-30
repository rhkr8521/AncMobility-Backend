package com.rhkr8521.ancmobility.api.alliance.repository;

import com.rhkr8521.ancmobility.api.alliance.entity.Alliance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllianceRepository extends JpaRepository<Alliance, Long> {
    // 추가 쿼리 메서드가 필요하면 작성합니다.
}
