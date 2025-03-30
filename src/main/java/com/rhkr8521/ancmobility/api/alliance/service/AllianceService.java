package com.rhkr8521.ancmobility.api.alliance.service;

import com.rhkr8521.ancmobility.api.alliance.dto.AllianceCreateDTO;
import com.rhkr8521.ancmobility.api.alliance.dto.AllianceListResponseDTO;
import com.rhkr8521.ancmobility.api.alliance.dto.AllianceResponseDTO;
import com.rhkr8521.ancmobility.api.alliance.entity.Alliance;
import com.rhkr8521.ancmobility.api.alliance.repository.AllianceRepository;
import com.rhkr8521.ancmobility.api.member.entity.Role;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.NotFoundException;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllianceService {

    private final AllianceRepository allianceRepository;

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

    // 제휴 생성
    @Transactional
    public void createAlliance(AllianceCreateDTO dto, MultipartFile imageFile, Role userRole) {
        validateAdmin(userRole);

        String imageUrl = storeImage(imageFile);

        Alliance alliance = Alliance.builder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .company(dto.getCompany())
                .active(dto.getActive())
                .tags(dto.getTags())
                .image(imageUrl)
                .build();

        allianceRepository.save(alliance);
    }

    // 제휴 수정
    @Transactional
    public void updateAlliance(Long id, AllianceCreateDTO dto, MultipartFile imageFile, Role userRole) {
        validateAdmin(userRole);

        Alliance alliance = allianceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ALLIANCE_NOTFOUND_EXCEPTION.getMessage()));

        // 이미지 업데이트: 새 파일이 있으면 기존 파일 삭제 후 저장, 없으면 기존 URL 유지
        String imageUrl = alliance.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            deleteImage(imageUrl);
            imageUrl = storeImage(imageFile);
        }

        alliance = alliance.toBuilder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .company(dto.getCompany())
                .active(dto.getActive())
                .tags(dto.getTags())
                .image(imageUrl)
                .build();

        allianceRepository.save(alliance);
    }

    // 제휴 목록 조회 (active와 inactive 각각 최신순 정렬)
    @Transactional(readOnly = true)
    public AllianceListResponseDTO getAllAlliances() {
        List<Alliance> alliances = allianceRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<AllianceResponseDTO> active = alliances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getActive()))
                .map(AllianceResponseDTO::fromEntity)
                .collect(Collectors.toList());

        List<AllianceResponseDTO> inactive = alliances.stream()
                .filter(a -> !Boolean.TRUE.equals(a.getActive()))
                .map(AllianceResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return AllianceListResponseDTO.builder()
                .active(active)
                .inactive(inactive)
                .build();
    }

    // 제휴 삭제
    @Transactional
    public void deleteAlliance(Long id, Role userRole) {
        validateAdmin(userRole);

        Alliance alliance = allianceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ALLIANCE_NOTFOUND_EXCEPTION.getMessage()));

        // 이미지 삭제
        deleteImage(alliance.getImage());

        allianceRepository.delete(alliance);
    }
}
