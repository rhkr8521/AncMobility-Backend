package com.rhkr8521.ancmobility.api.companyinfo.service;

import com.rhkr8521.ancmobility.api.companyinfo.dto.CompanyInfoRequestDTO;
import com.rhkr8521.ancmobility.api.companyinfo.dto.CompanyInfoResponseDTO;
import com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfo;
import com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfoType;
import com.rhkr8521.ancmobility.api.companyinfo.repository.CompanyInfoRepository;
import com.rhkr8521.ancmobility.api.member.entity.Role;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.NotFoundException;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
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

    // 이미지 저장 메서드 - timestamp(밀리초) + UUID를 사용해 고유한 파일명 생성
    private String storeImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }
        // 현재 날짜 기반 파일명 생성 (년월일_시분초_밀리초 + UUID)
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        String randomUUID = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = timestamp + "_" + randomUUID + extension;
        File dest = new File(imageServerPath, newFileName);

        // 부모 디렉토리가 없으면 생성
        if (!dest.getParentFile().exists()) {
            boolean dirsCreated = dest.getParentFile().mkdirs();
            if (!dirsCreated) {
                log.error("디렉토리 생성 실패: {}", dest.getParentFile().getAbsolutePath());
                throw new BadRequestException("이미지 저장 경로 디렉토리 생성에 실패하였습니다.");
            }
        }

        try {
            imageFile.transferTo(dest);
        } catch (IOException e) {
            log.error("이미지 저장 중 오류 발생 - 파일명: {}, 경로: {}, 에러: {}",
                    newFileName, dest.getAbsolutePath(), e.getMessage(), e);
            throw new BadRequestException(ErrorStatus.FAIL_IMAGE_UPLOAD_EXCEPTION.getMessage());
        }
        return "https://www.ancmobility.co.kr:81/api/images/" + newFileName;
    }

    // 기존 이미지 삭제 (URL에서 파일명을 추출하여 실제 파일 삭제)
    private void deleteImage(String imageUrl) {
        if (imageUrl == null) {
            return;
        }
        final String prefix = "https://www.ancmobility.co.kr:81/api/images/";
        if (!imageUrl.startsWith(prefix)) {
            return;
        }
        String fileName = imageUrl.substring(prefix.length());
        File file = new File(imageServerPath, fileName);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.warn("기존 이미지 삭제 실패: {}", file.getAbsolutePath());
            } else {
                log.info("기존 이미지 삭제 성공: {}", file.getAbsolutePath());
            }
        }
    }

    // 회사 정보 생성
    @Transactional
    public void createCompanyInfo(CompanyInfoRequestDTO dto,
                                  MultipartFile bannerImageFile,
                                  MultipartFile imageFile,
                                  Role userRole) {
        validateAdmin(userRole);

        // 이미 해당 타입의 정보가 존재하는지 확인 (한 타입당 1건만 등록 가능)
        if (companyInfoRepository.findByCompanyInfoType(dto.getCompanyInfoType()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_CREATE_COMPANYINFO_EXCEPTION.getMessage());
        }

        String bannerImageUrl = storeImage(bannerImageFile);
        String imageUrl = storeImage(imageFile);

        CompanyInfo companyInfo = CompanyInfo.builder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .bannerImage(bannerImageUrl)
                .image(imageUrl)
                .companyInfoType(dto.getCompanyInfoType())
                .build();

        CompanyInfo saved = companyInfoRepository.save(companyInfo);
        CompanyInfoResponseDTO.fromEntity(saved);
    }

    // 회사 정보 수정
    @Transactional
    public void updateCompanyInfo(CompanyInfoRequestDTO dto,
                                  MultipartFile bannerImageFile,
                                  MultipartFile imageFile,
                                  Role userRole) {
        validateAdmin(userRole);

        CompanyInfo companyInfo = companyInfoRepository.findByCompanyInfoType(dto.getCompanyInfoType())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMPANYINFO_TYPE_NOTFOUND_EXCEPTION.getMessage()));

        // 배너 이미지 업데이트: 새 파일이 있으면 기존 파일 삭제 후 저장, 없으면 기존 URL 유지
        String bannerImageUrl = companyInfo.getBannerImage();
        if (bannerImageFile != null && !bannerImageFile.isEmpty()) {
            deleteImage(bannerImageUrl);
            bannerImageUrl = storeImage(bannerImageFile);
        }

        // 일반 이미지 업데이트: 새 파일이 있으면 기존 파일 삭제 후 저장, 없으면 기존 URL 유지
        String imageUrl = companyInfo.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            deleteImage(imageUrl);
            imageUrl = storeImage(imageFile);
        }

        companyInfo = companyInfo.toBuilder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .bannerImage(bannerImageUrl)
                .image(imageUrl)
                .build();

        CompanyInfo updated = companyInfoRepository.save(companyInfo);
        CompanyInfoResponseDTO.fromEntity(updated);
    }

    // 회사 정보 조회
    @Transactional(readOnly = true)
    public CompanyInfoResponseDTO getCompanyInfoByType(CompanyInfoType companyInfoType) {
        CompanyInfo companyInfo = companyInfoRepository.findByCompanyInfoType(companyInfoType)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMPANYINFO_TYPE_NOTFOUND_EXCEPTION.getMessage()));
        return CompanyInfoResponseDTO.fromEntity(companyInfo);
    }
}