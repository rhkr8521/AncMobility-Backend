package com.rhkr8521.ancmobility.api.franchise.repository;

import com.rhkr8521.ancmobility.api.franchise.entity.Franchise;
import com.rhkr8521.ancmobility.api.franchise.entity.Settlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    Optional<Settlement> findByFranchiseAndSettlementDate(Franchise franchise, LocalDate settlementDate);
    List<Settlement> findBySettlementDateAndFranchise_Id(LocalDate settlementDate, Long franchiseId);
    Page<Settlement> findBySettlementDate(LocalDate settlementDate, Pageable pageable);

    @EntityGraph(attributePaths = "franchise")
    Page<Settlement> findByFranchise_NameAndSettlementDate(String franchiseName, LocalDate settlementDate, Pageable pageable);

    void deleteAllByFranchise(Franchise franchise);
}
