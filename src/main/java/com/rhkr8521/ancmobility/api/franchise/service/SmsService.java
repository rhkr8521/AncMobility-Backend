package com.rhkr8521.ancmobility.api.franchise.service;

import com.rhkr8521.ancmobility.api.franchise.dto.PhoneAuthRequestDTO;
import com.rhkr8521.ancmobility.api.franchise.dto.PhoneAuthResponseDTO;
import com.rhkr8521.ancmobility.api.franchise.entity.Franchise;
import com.rhkr8521.ancmobility.api.franchise.entity.PhoneNumberVerification;
import com.rhkr8521.ancmobility.api.franchise.repository.FranchiseRepository;
import com.rhkr8521.ancmobility.api.franchise.repository.PhoneNumberVerificationRepository;
import com.rhkr8521.ancmobility.api.member.dto.MemberLoginResponseDTO;
import com.rhkr8521.ancmobility.api.member.jwt.service.JwtService;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.InternalServerException;
import com.rhkr8521.ancmobility.common.exception.UnauthorizedException;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final FranchiseRepository franchiseRepository;
    private final PhoneNumberVerificationRepository phoneNumberVerificationRepository;
    private final JwtService jwtService;

    // 설정값 주입
    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String senderPhoneNumber;

    private DefaultMessageService messageService;

    // CoolSMS SDK 초기화
    public void initializeMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    // 가맹점 로그인
    @Transactional
    public void sendVerificationSms(PhoneAuthRequestDTO phoneAuthRequestDTO, LocalDateTime requestedAt) {

        initializeMessageService(); // 메시지 서비스 초기화

        // 기존 가맹점 체크
        if (franchiseRepository.findByNameAndPhoneNumber(phoneAuthRequestDTO.getName(), phoneAuthRequestDTO.getPhoneNumber()).isEmpty()) {
            throw new BadRequestException(ErrorStatus.MISSING_FRANCHISE_INFO_EXCEPTION.getMessage());
        }

        // 기존 인증코드 삭제
        phoneNumberVerificationRepository.findByPhoneNumber(phoneAuthRequestDTO.getPhoneNumber())
                .ifPresent(phoneNumberVerificationRepository::delete);

        // 새 인증코드 생성
        String code = generateSixDigitCode();
        PhoneNumberVerification verification = PhoneNumberVerification.builder()
                .phoneNumber(phoneAuthRequestDTO.getPhoneNumber())
                .code(code)
                .expirationTimeInMinutes(5)
                .isVerified(false)
                .build();
        phoneNumberVerificationRepository.save(verification);

        // SMS 발송
        Message message = new Message();
        message.setFrom(senderPhoneNumber);
        message.setTo(phoneAuthRequestDTO.getPhoneNumber());
        message.setText(String.format("[애니콜 모빌리티(주) 가맹점 조회]\n인증코드 : %s\n인증코드는 5분 후 만료됩니다.", code));

        try {
            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);
        } catch (Exception e) {
            throw new InternalServerException(ErrorStatus.SMS_SEND_FAILED_EXCEPTION.getMessage());
        }

    }

    private String generateSixDigitCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(1000000); // 0 ~ 999999
        return String.format("%06d", number); // 6자리 코드
    }

    // 인증 코드 검증
    @Transactional
    public PhoneAuthResponseDTO verifyCodeAndIssueToken(String code, LocalDateTime requestedAt) {
        PhoneNumberVerification verification = phoneNumberVerificationRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.WRONG_SMS_VERIFICATION_CODE_EXCEPTION.getMessage()));

        if (verification.isExpired(requestedAt)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_SMS_VERIFICATION_CODE_EXCEPTION.getMessage());
        }

        verification.setIsVerified(true);
        phoneNumberVerificationRepository.save(verification);

        Franchise franchise = franchiseRepository.findByPhoneNumber(verification.getPhoneNumber()).orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_FRANCHISE_INFO_EXCEPTION.getMessage()));

        String accessToken = jwtService.createFranchiseToken(franchise.getId());

        return new PhoneAuthResponseDTO(
                franchise.getName(),
                franchise.getPhoneNumber(),
                franchise.getCarNumber(),
                accessToken
        );
    }
}

