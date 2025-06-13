package com.rhkr8521.ancmobility.api.contact.service;

import com.rhkr8521.ancmobility.api.contact.dto.ContactCreateDTO;
import com.rhkr8521.ancmobility.api.contact.dto.ContactResponseDTO;
import com.rhkr8521.ancmobility.api.contact.entity.Contact;
import com.rhkr8521.ancmobility.api.contact.repository.ContactRepository;
import com.rhkr8521.ancmobility.api.home.dto.HomeRequestDTO;
import com.rhkr8521.ancmobility.api.home.dto.HomeResponseDTO;
import com.rhkr8521.ancmobility.api.home.entity.Home;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

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
        // UUID 기반 파일명 생성
        String uuid = java.util.UUID.randomUUID().toString();
        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = uuid + extension;
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

    // 문의처 정보 생성
    @Transactional
    public void createContactInfo(ContactCreateDTO contactCreateDTO,
                                   MultipartFile addressImage,
                                   Role userRole) {
        validateAdmin(userRole);

        // 이미 Contact 엔티티가 하나라도 존재하면 추가 생성 불가
        long count = contactRepository.count();
        if (count > 0) {
            throw new BadRequestException(ErrorStatus.ALREADY_CREATE_CONTRACT_EXCEPTION.getMessage());
        }

        // 이미지 업로드
        String addressImageFile = storeImage(addressImage);

        Contact contact = Contact.builder()
                .address(contactCreateDTO.getAddress())
                .addressImage(addressImageFile)
                .telephone(contactCreateDTO.getTelephone())
                .fax(contactCreateDTO.getFax())
                .email(contactCreateDTO.getEmail())
                .build();

        contactRepository.save(contact);
    }

    // 문의처 정보 수정
    @Transactional
    public void updateContactInfo(ContactCreateDTO contactCreateDTO,
                               MultipartFile addressImage,
                               Role userRole) {
        validateAdmin(userRole);

        // 현재 등록된 Home 정보가 있어야 수정 가능
        Optional<Contact> optionalContact = contactRepository.findAll().stream().findFirst();
        if (optionalContact.isEmpty()) {
            throw new NotFoundException(ErrorStatus.CONTACT_NOTFOUND_EXCEPTION.getMessage());
        }

        Contact existingContact = optionalContact.get();

        // 이미지 업데이트 (새 파일이 있으면 기존 파일 삭제 후 저장, 없으면 기존 URL 유지)
        String addressImageFile = existingContact.getAddressImage();
        if (addressImage != null && !addressImage.isEmpty()) {
            deleteImage(addressImageFile);
            addressImageFile = storeImage(addressImage);
        }

        existingContact = existingContact.toBuilder()
                .address(contactCreateDTO.getAddress())
                .addressImage(addressImageFile)
                .telephone(contactCreateDTO.getTelephone())
                .fax(contactCreateDTO.getFax())
                .email(contactCreateDTO.getEmail())
                .build();

        contactRepository.save(existingContact);
    }

    // 문의처 정보 조회
    @Transactional(readOnly = true)
    public ContactResponseDTO getContactInfo() {

        Optional<Contact> optionalContact = contactRepository.findAll().stream().findFirst();
        if (optionalContact.isEmpty()) {
            throw new NotFoundException(ErrorStatus.CONTACT_NOTFOUND_EXCEPTION.getMessage());
        }
        Contact contact = optionalContact.get();
        return ContactResponseDTO.fromEntity(contact);
    }

}
