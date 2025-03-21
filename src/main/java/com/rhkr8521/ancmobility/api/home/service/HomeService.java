package com.rhkr8521.ancmobility.api.home.service;

import com.rhkr8521.ancmobility.api.home.dto.HomeRequestDTO;
import com.rhkr8521.ancmobility.api.home.dto.HomeResponseDTO;
import com.rhkr8521.ancmobility.api.home.entity.Home;
import com.rhkr8521.ancmobility.api.home.repository.HomeRepository;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;

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
        // 이미지 서버에 접근할 수 있는 URL을 만들어서 DB에 저장
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

    // 홈 화면 생성
    @Transactional
    public void createHomeInfo(HomeRequestDTO dto,
                               MultipartFile bannerImageFile1,
                               MultipartFile bannerImageFile2,
                               MultipartFile bannerImageFile3,
                               Role userRole) {
        validateAdmin(userRole);

        // 이미 Home 엔티티가 하나라도 존재하면 추가 생성 불가
        long count = homeRepository.count();
        if (count > 0) {
            throw new BadRequestException(ErrorStatus.ALREADY_CREATE_HOMEINFO_EXCEPTION.getMessage());
        }

        // 이미지 업로드
        String bannerImageUrl1 = storeImage(bannerImageFile1);
        String bannerImageUrl2 = storeImage(bannerImageFile2);
        String bannerImageUrl3 = storeImage(bannerImageFile3);

        Home home = Home.builder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .bannerImage1(bannerImageUrl1)
                .bannerImage2(bannerImageUrl2)
                .bannerImage3(bannerImageUrl3)
                .build();

        homeRepository.save(home);
    }

    // 홈 화면 수정
    @Transactional
    public void updateHomeInfo(HomeRequestDTO dto,
                               MultipartFile bannerImageFile1,
                               MultipartFile bannerImageFile2,
                               MultipartFile bannerImageFile3,
                               Role userRole) {
        validateAdmin(userRole);

        // 현재 등록된 Home 정보가 있어야 수정 가능
        Optional<Home> optionalHome = homeRepository.findAll().stream().findFirst();
        if (optionalHome.isEmpty()) {
            throw new NotFoundException(ErrorStatus.HOMEINFO_NOTFOUND_EXCEPTION.getMessage());
        }

        Home existingHome = optionalHome.get();

        // 이미지 업데이트 (새 파일이 있으면 기존 파일 삭제 후 저장, 없으면 기존 URL 유지)
        String bannerImageUrl1 = existingHome.getBannerImage1();
        if (bannerImageFile1 != null && !bannerImageFile1.isEmpty()) {
            deleteImage(bannerImageUrl1);
            bannerImageUrl1 = storeImage(bannerImageFile1);
        }

        String bannerImageUrl2 = existingHome.getBannerImage2();
        if (bannerImageFile2 != null && !bannerImageFile2.isEmpty()) {
            deleteImage(bannerImageUrl2);
            bannerImageUrl2 = storeImage(bannerImageFile2);
        }

        String bannerImageUrl3 = existingHome.getBannerImage3();
        if (bannerImageFile3 != null && !bannerImageFile3.isEmpty()) {
            deleteImage(bannerImageUrl3);
            bannerImageUrl3 = storeImage(bannerImageFile3);
        }

        existingHome = existingHome.toBuilder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .bannerImage1(bannerImageUrl1)
                .bannerImage2(bannerImageUrl2)
                .bannerImage3(bannerImageUrl3)
                .build();

        homeRepository.save(existingHome);
    }

    // 홈 화면 조회
    @Transactional(readOnly = true)
    public HomeResponseDTO getHomeInfo() {

        Optional<Home> optionalHome = homeRepository.findAll().stream().findFirst();
        if (optionalHome.isEmpty()) {
            throw new NotFoundException(ErrorStatus.HOMEINFO_NOTFOUND_EXCEPTION.getMessage());
        }
        Home home = optionalHome.get();
        return HomeResponseDTO.fromEntity(home);
    }
}
