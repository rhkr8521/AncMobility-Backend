package com.rhkr8521.ancmobility.api.companyinfo.service;

import com.rhkr8521.ancmobility.api.companyinfo.dto.CompanyInfoRequestDTO;
import com.rhkr8521.ancmobility.api.companyinfo.dto.CompanyInfoResponseDTO;
import com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfo;
import com.rhkr8521.ancmobility.api.companyinfo.repository.CompanyInfoRepository;
import com.rhkr8521.ancmobility.api.member.entity.Role;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.NotFoundException;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CompanyInfoService {

    private final CompanyInfoRepository companyInfoRepository;

    @Value("${image.server}")
    private String imageServerPath;

    // ADMIN 권한 체크
    private void validateAdmin(Role userRole) {
        if (userRole != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }
    }

    // 이미지 저장
    private String storeImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }
        // 현재 날짜 기반 파일명 생성 (년월일_시분초)
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = timestamp + extension;
        File dest = new File(imageServerPath, newFileName);
        try {
            imageFile.transferTo(dest);
        } catch (IOException e) {
            throw new BadRequestException(ErrorStatus.FAIL_IMAGE_UPLOAD_EXCEPTION.getMessage());
        }
        return newFileName;
    }

    // 회사 정보 생성
    @Transactional
    public void createCompanyInfo(CompanyInfoRequestDTO dto, Role userRole) {
        validateAdmin(userRole);

        if (companyInfoRepository.findByCompanyInfoType(dto.getCompanyInfoType()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_CREATE_COMPANYINFO_EXCEPTION.getMessage());
        }

        String savedImageName = storeImage(dto.getImage());

        CompanyInfo companyInfo = CompanyInfo.builder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .image(savedImageName)
                .companyInfoType(dto.getCompanyInfoType())
                .build();

        CompanyInfo saved = companyInfoRepository.save(companyInfo);
        CompanyInfoResponseDTO.fromEntity(saved);
    }

    // 회사 정보 수정
    @Transactional
    public void updateCompanyInfo(CompanyInfoRequestDTO dto, Role userRole) {
        validateAdmin(userRole);

        CompanyInfo companyInfo = companyInfoRepository.findByCompanyInfoType(dto.getCompanyInfoType())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMPANYINFO_TYPE_NOTFOUND_EXCEPTION.getMessage()));

        String savedImageName = companyInfo.getImage();
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            savedImageName = storeImage(dto.getImage());
        }

        companyInfo = companyInfo.toBuilder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .image(savedImageName)
                .build();

        CompanyInfo updated = companyInfoRepository.save(companyInfo);
        CompanyInfoResponseDTO.fromEntity(updated);
    }

    // 회사 정보 조회
    @Transactional(readOnly = true)
    public CompanyInfoResponseDTO getCompanyInfoByType(com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfoType companyInfoType) {

        CompanyInfo companyInfo = companyInfoRepository.findByCompanyInfoType(companyInfoType)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMPANYINFO_TYPE_NOTFOUND_EXCEPTION.getMessage()));
        return CompanyInfoResponseDTO.fromEntity(companyInfo);
    }
}
