package com.rhkr8521.ancmobility.api.news.service;

import com.rhkr8521.ancmobility.api.member.entity.Member;
import com.rhkr8521.ancmobility.api.member.entity.Role;
import com.rhkr8521.ancmobility.api.member.repository.MemberRepository;
import com.rhkr8521.ancmobility.api.news.dto.NewsCreateDTO;
import com.rhkr8521.ancmobility.api.news.dto.NewsDetailDTO;
import com.rhkr8521.ancmobility.api.news.dto.NewsListDTO;
import com.rhkr8521.ancmobility.api.news.dto.NewsPageResponseDTO;
import com.rhkr8521.ancmobility.api.news.entity.News;
import com.rhkr8521.ancmobility.api.news.repository.NewsRepository;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.NotFoundException;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;

    @Value("${image.server}")
    private String imageServerPath;

    // base64 이미지 정규식
    private static final String BASE64_IMAGE_REGEX = "data:image/(png|jpeg|jpg|webp|bmp);base64,([A-Za-z0-9+/=]+)";

    // 뉴스 목록 조회 (최신순)
    public NewsPageResponseDTO getNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<News> news = newsRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<NewsListDTO> newsListDTOS = news.map(NewsListDTO::from);

        return NewsPageResponseDTO.builder()
                .totalElements(news.getTotalElements())
                .totalPages(news.getTotalPages())
                .page(news.getNumber())
                .size(news.getSize())
                .content(newsListDTOS.getContent())
                .build();
    }

    // 뉴스 상세 조회 (조회 시 조회수 증가)
    public NewsDetailDTO getNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NEWS_NOTFOUND_EXCEPTION.getMessage()));

        newsRepository.incrementViewCount(id);

        return NewsDetailDTO.from(news);
    }

    // 뉴스 등록 (ADMIN 권한 필요)
    @Transactional
    public void createNews(NewsCreateDTO createDTO, Long userId) {
        // 사용자 검증 (ADMIN만 가능)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        // 뉴스 본문(content)에서 첫 번째 base64 이미지 추출 및 서버에 저장
        String imageUrl = extractAndStoreFirstBase64Image(createDTO.getContent());

        News news = News.builder()
                .title(createDTO.getTitle())
                .content(createDTO.getContent())
                .subTitle(createDTO.getSubTitle())
                .image(imageUrl)
                .author(user)
                .viewCnt(0)
                .build();

        newsRepository.save(news);
    }

    // 뉴스 수정 (ADMIN 권한 필요)
    @Transactional
    public News updateNews(Long id, NewsCreateDTO newsCreateDTO, Long userId) {
        // 사용자 검증 (ADMIN만 가능)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NEWS_NOTFOUND_EXCEPTION.getMessage()));

        // 새로운 본문에서 base64 이미지 추출 및 저장
        String newImageUrl = extractAndStoreFirstBase64Image(newsCreateDTO.getContent());

        // 새 이미지가 있다면 기존 이미지를 삭제하고 교체, 없으면 기존 이미지가 있으면 삭제
        if (newImageUrl != null) {
            if (news.getImage() != null) {
                deleteImage(news.getImage());
            }
        } else {
            if (news.getImage() != null) {
                deleteImage(news.getImage());
            }
        }

        // 뉴스 내용 업데이트
        news = news.toBuilder()
                .title(newsCreateDTO.getTitle())
                .content(newsCreateDTO.getContent())
                .subTitle(newsCreateDTO.getSubTitle())
                .image(newImageUrl)
                .build();

        return newsRepository.save(news);
    }

    // 뉴스 삭제 (ADMIN 권한 필요)
    @Transactional
    public void deleteNews(Long id, Long userId) {
        // 사용자 검증 (ADMIN만 가능)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NEWS_NOTFOUND_EXCEPTION.getMessage()));

        // 뉴스에 저장된 이미지가 있다면 삭제
        if (news.getImage() != null) {
            deleteImage(news.getImage());
        }

        newsRepository.delete(news);
    }

    // 본문(content)에서 첫 번째 base64 인코딩 이미지 추출 후 서버에 저장하고 URL 반환
    private String extractAndStoreFirstBase64Image(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile(BASE64_IMAGE_REGEX);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String mimeType = matcher.group(1); // 예: png, jpeg 등
            String base64Data = matcher.group(2);
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            String extension = "." + mimeType;
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(decodedBytes);
            } catch (IOException e) {
                log.error("이미지 저장 중 오류 발생 - 파일명: {}, 경로: {}, 에러: {}",
                        newFileName, dest.getAbsolutePath(), e.getMessage(), e);
                throw new BadRequestException(ErrorStatus.FAIL_IMAGE_UPLOAD_EXCEPTION.getMessage());
            }
            return "https://www.ancmobility.co.kr:81/api/images/" + newFileName;
        }
        return null;
    }

    // 기존 이미지 삭제 (이미지 URL에서 파일명을 추출하여 실제 파일 삭제)
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
}
