package com.rhkr8521.ancmobility.api.franchise.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class SettlementAdminResponseDTO {

    private Long id;
    private LocalDate settlementDate;

    private String name;
    private String phoneNumber;
    private String carNumber;

    private BigDecimal totalFreightRevenue;
    private BigDecimal amountToBePaid;
    private BigDecimal marketingFeeSupplyValue;
    private BigDecimal marketingFeeVat;
    private BigDecimal marketingFeeTotal;
    private BigDecimal extraServiceFeeSupplyValue;
    private BigDecimal extraServiceFeeVat;
    private BigDecimal extraServiceFeeTotal;
}
