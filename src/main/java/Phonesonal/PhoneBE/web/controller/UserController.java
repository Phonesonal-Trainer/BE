package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.repository.DiagnosisRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.AI.GeminiService;
import Phonesonal.PhoneBE.web.dto.DiagnosisResultDTO;
import Phonesonal.PhoneBE.web.dto.Mypage.MypageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mypage")
@Tag(name = "Mypage", description = "마이페이지 관련 API")
public class UserController {
    private final GeminiService geminiService;
    private final DiagnosisRepository diagnosisRepository;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "마이페이지 홈화면 조회 API", description = "마이페이지 홈화면")
    public ApiResponse<MypageResponseDTO.HomeResponse> mypage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        LocalDateTime created = user.getCreated_at();
        LocalDateTime now = LocalDateTime.now();
        long weeksTogether = ChronoUnit.WEEKS.between(created, now)+1;
        MypageResponseDTO.HomeResponse response = MypageResponseDTO.HomeResponse.builder()
                .nickname(user.getNickname())
                .togetherWeeks(weeksTogether)
                .targetWeeks(user.getDeadline())
                .weight(user.getWeight())
                .bodyFatRate(user.getBodyFatRate())
                .BMI(geminiService.calculateBMI(user.getWeight(), user.getHeight()))
//                .muscleMass()
                .build();
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/target")
    @Operation(summary = "마이페이지 진단결과(목표수치) 조회 API", description = "마이페이지 진단결과 화면")
    public ApiResponse<DiagnosisResultDTO> target(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Long userId = user.getId();
        Diagnosis diagnosis = diagnosisRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("진단 데이터를 찾을 수 없습니다"));

        DiagnosisResultDTO response = DiagnosisResultDTO.builder()
                .weight(user.getWeight())
                .targetWeight(diagnosis.getTargetWeight())
                .BMI(geminiService.calculateBMI(user.getWeight(), user.getHeight()))
                .targetBMI(diagnosis.getTargetBMI())
                .targetMuscleMass(diagnosis.getTargetMuscleMass())
                .bodyFatRate(user.getBodyFatRate())
                .targetBodyFatRate(diagnosis.getTargetBodyFatRate())
                .recommendedNutrition(diagnosis.getRecommendedNutrition())
                .recommendedCalories(diagnosis.getRecommendedCalories())
                .workoutFrequency(diagnosis.getWorkoutFrequency())
                .cardioDaysPerWeek(diagnosis.getCardioDaysPerWeek())
                .cardioMinutesPerWeek(diagnosis.getCardioMinutesPerWeek())
                .strengthTrainingDays(diagnosis.getStrengthTrainingDays())
                .strengthTrainingTime(diagnosis.getStrengthTrainingTime())
                .overallRecommendation(diagnosis.getOverallRecommendation())
                .build();

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "마이페이지 개인정보 조회 API", description = "")
    public ApiResponse<MypageResponseDTO.ProfileResponse> kakaoCallback(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        MypageResponseDTO.ProfileResponse response = MypageResponseDTO.ProfileResponse.builder()
                .nickname(user.getNickname())
                .age(user.getAge())
                .gender(user.getGender())
                .height(user.getHeight())
                .build();
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 변경 API", description = "닉네임 변경")
    public ApiResponse<String> changeNickname(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestParam String nickname) {
        try {
            User user = userDetails.getUser();
            String oldNickname = user.getNickname();

            user.setNickname(nickname);
            User savedUser = userRepository.save(user);

            // 저장 결과 확인
            if (savedUser == null || !nickname.equals(savedUser.getNickname())) {
                return ApiResponse.onFailure("SAVE_FAILED", "닉네임 변경에 실패했습니다.",null);
            }

            return ApiResponse.onSuccess("닉네임이 수정되었습니다.");

        } catch (DataIntegrityViolationException e) {
            log.error("닉네임 중복 또는 제약조건 위반: {}", e.getMessage());
            return ApiResponse.onFailure("NICKNAME_DUPLICATE", "이미 사용중인 닉네임입니다.",null);
        } catch (DataAccessException e) {
            log.error("데이터베이스 접근 오류: {}", e.getMessage());
            return ApiResponse.onFailure("DATABASE_ERROR", "데이터베이스 오류가 발생했습니다.",null);
        } catch (Exception e) {
            log.error("닉네임 변경 중 예상치 못한 오류: {}", e.getMessage(), e);
            return ApiResponse.onFailure("INTERNAL_SERVER_ERROR", "시스템 오류가 발생했습니다.",null);
        }
    }

    @PatchMapping("/height")
    @Operation(summary = "신장 변경 API", description = "신장 변경")
    public ApiResponse<String> changeHeight(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestParam int height) {
        try {
            User user = userDetails.getUser();
            BigDecimal heightDecimal = new BigDecimal(height);
            BigDecimal oldHeight = user.getHeight();

            user.setHeight(heightDecimal);
            User savedUser = userRepository.save(user);

            // 저장 결과 확인
            if (savedUser == null || !heightDecimal.equals(savedUser.getHeight())) {
                return ApiResponse.onFailure("SAVE_FAILED", "신장 변경에 실패했습니다.",null);
            }

            return ApiResponse.onSuccess("신장이 수정되었습니다.");

        } catch (DataAccessException e) {
            log.error("데이터베이스 접근 오류: {}", e.getMessage());
            return ApiResponse.onFailure("DATABASE_ERROR", "데이터베이스 오류가 발생했습니다.",null);
        } catch (ArithmeticException e) {
            log.error("BigDecimal 연산 오류: {}", e.getMessage());
            return ApiResponse.onFailure("CALCULATION_ERROR", "신장 값 처리 중 오류가 발생했습니다.",null);
        } catch (Exception e) {
            log.error("신장 변경 중 예상치 못한 오류: {}", e.getMessage(), e);
            return ApiResponse.onFailure("INTERNAL_SERVER_ERROR", "시스템 오류가 발생했습니다.",null);
        }
    }
}
