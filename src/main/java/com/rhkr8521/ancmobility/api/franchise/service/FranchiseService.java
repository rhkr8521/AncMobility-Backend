package com.rhkr8521.ancmobility.api.franchise.service;

import com.rhkr8521.ancmobility.api.franchise.dto.*;
import com.rhkr8521.ancmobility.api.franchise.entity.Franchise;
import com.rhkr8521.ancmobility.api.franchise.entity.Settlement;
import com.rhkr8521.ancmobility.api.franchise.repository.FranchiseRepository;
import com.rhkr8521.ancmobility.api.franchise.repository.SettlementRepository;
import com.rhkr8521.ancmobility.api.member.entity.Member;
import com.rhkr8521.ancmobility.api.member.entity.Role;
import com.rhkr8521.ancmobility.api.member.repository.MemberRepository;
import com.rhkr8521.ancmobility.api.news.dto.NewsListDTO;
import com.rhkr8521.ancmobility.api.news.dto.NewsPageResponseDTO;
import com.rhkr8521.ancmobility.api.news.entity.News;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.NotFoundException;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.InternalException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final SettlementRepository settlementRepository;
    private final FranchiseRepository franchiseRepository;
    private final MemberRepository memberRepository;

    // 정산 내역 등록
    @Transactional
    public void importFromExcel(MultipartFile file, Long userId) {

        // 사용자 권한 확인
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();

            // 1) 연/월 파싱
            Row dateRow = sheet.getRow(0);
            int year  = Integer.parseInt(fmt.formatCellValue(dateRow.getCell(2)).replace("년","").trim());
            int month = Integer.parseInt(fmt.formatCellValue(dateRow.getCell(3)).replace("월","").trim());
            LocalDate settlementDate = LocalDate.of(year, month, 1);

            List<Settlement> upserts = new ArrayList<>();

            // 2) 데이터 행(인덱스 2~) 순회
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = fmt.formatCellValue(row.getCell(0)).trim();
                if (name.isEmpty()) continue;

                String carNumber = fmt.formatCellValue(row.getCell(1)).trim();
                String rawPhone   = fmt.formatCellValue(row.getCell(2)).trim();
                String phone = rawPhone.startsWith("0") ? rawPhone : "0" + rawPhone;

                Franchise franchise = franchiseRepository
                        .findByNameAndPhoneNumber(name, phone)
                        .orElseThrow(() -> new RuntimeException(
                                String.format("가맹점 없음: %s / %s", name, phone)
                        ));

                // 금액 필드 파싱
                BigDecimal marketingTotal       = parseDecimal(row.getCell(3), fmt);
                BigDecimal marketingSupplyValue = parseDecimal(row.getCell(4), fmt);
                BigDecimal marketingVat         = parseDecimal(row.getCell(5), fmt);
                BigDecimal extraTotal           = parseDecimal(row.getCell(6), fmt);
                BigDecimal extraSupplyValue     = parseDecimal(row.getCell(7), fmt);
                BigDecimal extraVat             = parseDecimal(row.getCell(8), fmt);
                BigDecimal amountToBePaid       = parseDecimal(row.getCell(9), fmt);
                BigDecimal totalFreightRevenue  = parseDecimal(row.getCell(10), fmt);

                // 기존 데이터가 있으면 덮어쓰기, 없으면 새로 생성
                Settlement settlement = settlementRepository
                        .findByFranchiseAndSettlementDate(franchise, settlementDate)
                        .map(existing -> existing.toBuilder()
                                .marketingFeeTotal(marketingTotal)
                                .marketingFeeSupplyValue(marketingSupplyValue)
                                .marketingFeeVat(marketingVat)
                                .extraServiceFeeTotal(extraTotal)
                                .extraServiceFeeSupplyValue(extraSupplyValue)
                                .extraServiceFeeVat(extraVat)
                                .amountToBePaid(amountToBePaid)
                                .totalFreightRevenue(totalFreightRevenue)
                                .build()
                        )
                        .orElseGet(() -> Settlement.builder()
                                .settlementDate(settlementDate)
                                .franchise(franchise)
                                .marketingFeeTotal(marketingTotal)
                                .marketingFeeSupplyValue(marketingSupplyValue)
                                .marketingFeeVat(marketingVat)
                                .extraServiceFeeTotal(extraTotal)
                                .extraServiceFeeSupplyValue(extraSupplyValue)
                                .extraServiceFeeVat(extraVat)
                                .amountToBePaid(amountToBePaid)
                                .totalFreightRevenue(totalFreightRevenue)
                                .build()
                        );

                upserts.add(settlement);
            }
            settlementRepository.saveAll(upserts);

        } catch (IOException e) {
            throw new InternalException(ErrorStatus.FAIL_UPLOAD_SETTLEMENT_EXCEPTION.getMessage());
        }
    }

    // 가맹점 목록 조회 (최신순)
    @Transactional(readOnly = true)
    public FranchiseListResponseDTO getFranchiseList(int page, int size, Long userId) {

        // 사용자 권한 확인
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Franchise> franchises  = franchiseRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<FranchiseListDTO> franchiseListDTOS = franchises.map(FranchiseListDTO::from);

        return FranchiseListResponseDTO.builder()
                .totalElements(franchises.getTotalElements())
                .totalPages(franchises.getTotalPages())
                .page(franchises.getNumber())
                .size(franchises.getSize())
                .content(franchiseListDTOS.getContent())
                .build();
    }

    // 사용자용: 날짜별 전체 정산 내역 조회
    @Transactional(readOnly = true)
    public SettlementListResponseDTO<SettlementResponseDTO> getSettlementByDate(
            LocalDate date, Long franchiseId) {

        List<SettlementResponseDTO> content = settlementRepository
                .findBySettlementDateAndFranchise_Id(date, franchiseId)
                .stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());

        return SettlementListResponseDTO.<SettlementResponseDTO>builder()
                .totalElements(content.size())
                .totalPages(1)
                .page(0)
                .size(content.size())
                .content(content)
                .build();
    }

    // 관리자용: 날짜별 페이징 정산 내역 조회
    @Transactional(readOnly = true)
    public SettlementListResponseDTO<SettlementAdminResponseDTO> getSettlementAdminByDate(
            LocalDate date, int page, int size, Long userId) {

        // 사용자 권한 확인
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Page<Settlement> pageEntity = settlementRepository
                .findBySettlementDate(date, PageRequest.of(page, size));

        List<SettlementAdminResponseDTO> content = pageEntity
                .getContent()
                .stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());

        return SettlementListResponseDTO.<SettlementAdminResponseDTO>builder()
                .totalElements(pageEntity.getTotalElements())
                .totalPages(pageEntity.getTotalPages())
                .page(pageEntity.getNumber())
                .size(pageEntity.getSize())
                .content(content)
                .build();
    }

    // 매출 내역 검색
    public SettlementListResponseDTO<SettlementAdminResponseDTO> getSearchSettlement(
            String name, LocalDate date, int page, int size, Long userId){

        // 사용자 권한 확인
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Page<Settlement> pageEntity = settlementRepository
                .findByFranchise_NameAndSettlementDate(name, date, PageRequest.of(page, size));

        List<SettlementAdminResponseDTO> content = pageEntity
                .getContent()
                .stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());

        return SettlementListResponseDTO.<SettlementAdminResponseDTO>builder()
                .totalElements(pageEntity.getTotalElements())
                .totalPages(pageEntity.getTotalPages())
                .page(pageEntity.getNumber())
                .size(pageEntity.getSize())
                .content(content)
                .build();
    }

    private SettlementResponseDTO toUserDto(Settlement s) {
        return SettlementResponseDTO.builder()
                .id(s.getId())
                .settlementDate(s.getSettlementDate())
                .totalFreightRevenue(s.getTotalFreightRevenue())
                .amountToBePaid(s.getAmountToBePaid())
                .marketingFeeSupplyValue(s.getMarketingFeeSupplyValue())
                .marketingFeeVat(s.getMarketingFeeVat())
                .marketingFeeTotal(s.getMarketingFeeTotal())
                .extraServiceFeeSupplyValue(s.getExtraServiceFeeSupplyValue())
                .extraServiceFeeVat(s.getExtraServiceFeeVat())
                .extraServiceFeeTotal(s.getExtraServiceFeeTotal())
                .build();
    }

    private SettlementAdminResponseDTO toAdminDto(Settlement s) {
        return SettlementAdminResponseDTO.builder()
                .id(s.getId())
                .settlementDate(s.getSettlementDate())
                .name(s.getFranchise().getName())
                .phoneNumber(s.getFranchise().getPhoneNumber())
                .carNumber(s.getFranchise().getCarNumber())
                .totalFreightRevenue(s.getTotalFreightRevenue())
                .amountToBePaid(s.getAmountToBePaid())
                .marketingFeeSupplyValue(s.getMarketingFeeSupplyValue())
                .marketingFeeVat(s.getMarketingFeeVat())
                .marketingFeeTotal(s.getMarketingFeeTotal())
                .extraServiceFeeSupplyValue(s.getExtraServiceFeeSupplyValue())
                .extraServiceFeeVat(s.getExtraServiceFeeVat())
                .extraServiceFeeTotal(s.getExtraServiceFeeTotal())
                .build();
    }

    // 가맹점 등록
    @Transactional
    public void createFranchise(FranchiseCreateRequestDTO franchiseCreateRequestDTO, Long userId) {

        // 사용자 권한 확인
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        franchiseRepository.findByNameAndPhoneNumber(franchiseCreateRequestDTO.getName(), franchiseCreateRequestDTO.getPhoneNumber())
                .ifPresent(f -> {
                    throw new BadRequestException(
                            ErrorStatus.VALIDATION_DUPLICATE_FRANCHISE_EXCEPTION.getMessage()
                    );
                });

        Franchise f = Franchise.builder()
                .name(franchiseCreateRequestDTO.getName())
                .phoneNumber(franchiseCreateRequestDTO.getPhoneNumber())
                .carNumber(franchiseCreateRequestDTO.getCarNumber())
                .build();

        franchiseRepository.save(f);
    }

    // 가맹점 수정
    @Transactional
    public void updateFranchise(Long id, FranchiseCreateRequestDTO franchiseCreateRequestDTO, Long userId) {

        // 사용자 권한 확인
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Franchise existing = franchiseRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        ErrorStatus.NOT_FOUND_FRANCHISE_EXCEPTION.getMessage()
                ));
        Franchise updated = existing.toBuilder()
                .name(franchiseCreateRequestDTO.getName())
                .phoneNumber(franchiseCreateRequestDTO.getPhoneNumber())
                .carNumber(franchiseCreateRequestDTO.getCarNumber())
                .build();

        franchiseRepository.save(updated);
    }

    // 가맹점 삭제
    @Transactional
    public void deleteFranchise(Long id, Long userId) {

        // 사용자 권한 확인
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Franchise existing = franchiseRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        ErrorStatus.NOT_FOUND_FRANCHISE_EXCEPTION.getMessage()
                ));

        // 정산 내역 모두 삭제
        settlementRepository.deleteAllByFranchise(existing);
        franchiseRepository.delete(existing);
    }

    // 정산 내역 삭제
    @Transactional
    public void deleteSettlement(Long id, Long userId){

        // 사용자 권한 확인
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN){
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        // 정산 내역 삭제
        settlementRepository.deleteById(id);
    }

    // 가맹점 검색
    @Transactional(readOnly = true)
    public FranchiseListResponseDTO getSearchFranchise(String name, int page, int size, Long userId){

        // 사용자 권한 확인
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN){
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Franchise> franchises  = franchiseRepository.findByNameOrderByCreatedAtDesc(name, pageable);
        Page<FranchiseListDTO> franchiseListDTOS = franchises.map(FranchiseListDTO::from);

        return FranchiseListResponseDTO.builder()
                .totalElements(franchises.getTotalElements())
                .totalPages(franchises.getTotalPages())
                .page(franchises.getNumber())
                .size(franchises.getSize())
                .content(franchiseListDTOS.getContent())
                .build();
    }

    private BigDecimal parseDecimal(Cell cell, DataFormatter fmt) {
        if (cell == null) return BigDecimal.ZERO;
        String txt = fmt.formatCellValue(cell).trim();
        try {
            return txt.isEmpty() ? BigDecimal.ZERO : new BigDecimal(txt);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
