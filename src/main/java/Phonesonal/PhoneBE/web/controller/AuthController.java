package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.Gender;
import Phonesonal.PhoneBE.domain.enums.Purpose;
import Phonesonal.PhoneBE.domain.enums.SocialType;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.security.JwtTokenProvider;
import Phonesonal.PhoneBE.service.Auth.KakaoService;
import Phonesonal.PhoneBE.web.dto.Auth.KakaoRequestDTO;
import Phonesonal.PhoneBE.web.dto.Auth.LoginResultDTO;
import Phonesonal.PhoneBE.web.dto.InfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "로그인 및 회원가입 관련 API")
public class AuthController {

    private final KakaoService kakaoService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoalPeriodRepository goalPeriodRepository;

    @GetMapping("/kakao/login")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        // 코드만 전달
        response.sendRedirect("/auth/kakao/success?code=" + code);
    }

    @GetMapping("/kakao/success")
    public void authSuccess(@RequestParam String code, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(
                "{\"success\": true, \"authCode\": \"" + code + "\"}"
        );
    }

    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 로그인 API", description = "카카오 로그인")
    public ApiResponse<LoginResultDTO> userCheck(@RequestBody KakaoRequestDTO request) {
        String authCode = request.getAuthCode();
        String accessToken = kakaoService.getAccessToken(authCode);
        Map<String, Object> kakaoUserInfo = kakaoService.getUserInfo(accessToken);

        String kakaoEmail = kakaoUserInfo.get("email").toString();
        Optional<User> user = userRepository.findByEmail(kakaoEmail);

        if (user.isPresent()) {
            String jwtAccessToken = jwtTokenProvider.createToken(kakaoEmail);
            String jwtRefreshToken = jwtTokenProvider.createRefreshToken(kakaoEmail);
            LoginResultDTO response = LoginResultDTO.builder()
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .user(user.get())
                    .isNewUser(false)
                    .build();
            return ApiResponse.onSuccess(response);
        } else {
            // 임시 토큰 생성 (짧은 만료시간 설정)
            String tempToken = jwtTokenProvider.createTempToken(kakaoEmail, kakaoUserInfo, SocialType.KAKAO);
            LoginResultDTO response = LoginResultDTO.builder()
                    .tempToken(tempToken)
                    .isNewUser(true)
                    .build();
            return ApiResponse.onSuccess(response);
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "신규회원 정보 기입, 가입")
    public ApiResponse<LoginResultDTO> signup(
            @RequestParam String tempToken,
            @RequestParam String nickname,
            @RequestParam int age,
            @RequestParam Gender gender,
            @RequestParam Purpose purpose,
            @RequestParam int deadline,
            @RequestParam BigDecimal height,
            @RequestParam BigDecimal weight,
            @RequestParam(required = false) BigDecimal bodyFatRate,
            @RequestParam(required = false) BigDecimal muscleMass
    ) {
        try {
            // 임시 토큰에서 사용자 정보 추출
            Map<String, Object> tokenResult = jwtTokenProvider.getUserInfoFromTempToken(tempToken);
            Map<String, Object> userInfo = (Map<String, Object>) tokenResult.get("userInfo");
            SocialType socialType = (SocialType) tokenResult.get("socialType");

            String userName;
            String userEmail;
            String profileImageUrl;

            if (socialType == SocialType.GOOGLE) {
                userName = (String) userInfo.get("name");  // 구글은 "name"
                userEmail = (String) userInfo.get("email");
                profileImageUrl = (String) userInfo.get("picture");
            } else {
                // 카카오 처리 (기존 로직)
                userName = (String) userInfo.get("nickname");
                userEmail = (String) userInfo.get("email");
                profileImageUrl = (String) userInfo.get("profileImage");
            }

            // 이미 가입된 유저인지 재확인
            if (userRepository.findByEmail(userEmail).isPresent()) {
                return ApiResponse.onFailure("USER_ALREADY_EXISTS", "이미 가입된 사용자입니다", null);
            }

            // 새 유저 생성
            User newUser = User.builder()
                    .name(userName)
                    .email(userEmail)
                    .nickname(nickname)
                    .age(age)
                    .gender(gender)
                    .purpose(purpose)
                    .deadline(deadline)
                    .profileImageUrl(profileImageUrl)
                    .height(height) // BigDecimal -> int 변환
                    .weight(weight) // BigDecimal -> int 변환
                    .bodyFatRate(bodyFatRate) // BigDecimal -> Double 변환
                    .muscleMass(muscleMass) // BigDecimal -> Double 변환
                    .socialType(socialType)
                    .created_at(LocalDateTime.now())
                    .build();

            User savedUser = userRepository.save(newUser);
            LocalDate startDate = LocalDate.now();
            LocalDate targetDate = startDate.plusMonths(deadline);

            LocalDate endDate = targetDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            GoalPeriod goalPeriod = GoalPeriod.builder()
                    .user(savedUser)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            GoalPeriod savedGoaldPeriod = goalPeriodRepository.save(goalPeriod);

            savedUser.setGoalPeriod(savedGoaldPeriod);
            userRepository.save(savedUser);

            // 정식 JWT 토큰 발급
            String jwtAccessToken = jwtTokenProvider.createToken(userEmail);
            String jwtRefreshToken = jwtTokenProvider.createRefreshToken(userEmail);

            LoginResultDTO response = LoginResultDTO.builder()
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .user(newUser)
                    .isNewUser(false)
                    .build();

            return ApiResponse.onSuccess(response);

        } catch (Exception e) {
            System.out.println("=== 토큰 파싱 에러 ===");
            System.out.println("에러 타입: " + e.getClass().getSimpleName());
            System.out.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            System.out.println("==================");
            return ApiResponse.onFailure("INVALID_TEMP_TOKEN", "유효하지 않은 임시 토큰입니다", null);
        }
    }

    @GetMapping("/me")
    @Operation(summary = "정보 조회 API", description = "정보 조회")
    public ApiResponse<InfoResponse> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        LocalDateTime created = user.getCreated_at();
        LocalDateTime now = LocalDateTime.now();
        long weeksTogether = ChronoUnit.WEEKS.between(created, now)+1;
        InfoResponse response = InfoResponse.builder()
                .nickName(user.getNickname())
                .height(user.getHeight())
                .weight(user.getWeight())
                .deadline(user.getDeadline())
                .email(user.getEmail())
                .weeksTogether(weeksTogether)
                .build();
        return ApiResponse.onSuccess(response);
    }
}