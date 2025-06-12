package com.rhkr8521.ancmobility.api.franchise.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class SettlementResponseDTO {

    private Long id; // 정산 ID
    private LocalDate settlementDate; // 정산 날짜

    private BigDecimal totalFreightRevenue; // 총 운임 매출
    private BigDecimal amountToBePaid; // 납부 할 금액
    private BigDecimal marketingFeeSupplyValue; // 계속가맹금: 공급가액
    private BigDecimal marketingFeeVat; // 계속가맹금: 부가세
    private BigDecimal marketingFeeTotal; // 계속가맹금: 총 합계 (공급가액 + 부가세)
    private BigDecimal extraServiceFeeSupplyValue; // 서비스호출료: 공급가액
    private BigDecimal extraServiceFeeVat; // 서비스호출료: 부가세
    private BigDecimal extraServiceFeeTotal; // 서비스호출료: 총 합계 (공급가액 + 부가세)


}
