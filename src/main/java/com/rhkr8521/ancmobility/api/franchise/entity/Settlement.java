package com.rhkr8521.ancmobility.api.franchise.entity;

import com.rhkr8521.ancmobility.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "settlement")
public class Settlement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate settlementDate;

    /**
     * 어느 가맹점의 정산 내역인지
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    /**
     * 총 운임 매출
     */
    @Column(name = "total_freight_revenue", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalFreightRevenue;

    /**
     * 납부할 금액 (플랫폼에 납부할 운임)
     */
    @Column(name = "amount_to_be_paid", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountToBePaid;

    /**
     * 계속가맹금: 공급가액
     */
    @Column(name = "marketing_fee_supply_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal marketingFeeSupplyValue;

    /**
     * 계속가맹금: 부가세
     */
    @Column(name = "marketing_fee_vat", nullable = false, precision = 15, scale = 2)
    private BigDecimal marketingFeeVat;

    /**
     * 계속가맹금: 총 합계 (공급가액 + 부가세)
     */
    @Column(name = "marketing_fee_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal marketingFeeTotal;

    /**
     * 서비스호출료: 공급가액
     */
    @Column(name = "extra_service_fee_supply_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal extraServiceFeeSupplyValue;

    /**
     * 서비스호출료: 부가세
     */
    @Column(name = "extra_service_fee_vat", nullable = false, precision = 15, scale = 2)
    private BigDecimal extraServiceFeeVat;

    /**
     * 서비스호출료: 총 합계 (공급가액 + 부가세)
     */
    @Column(name = "extra_service_fee_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal extraServiceFeeTotal;
}