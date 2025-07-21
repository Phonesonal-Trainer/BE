package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.enums.SocialType;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.security.CustomOAuth2UserService;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.security.JwtTokenProvider;
import Phonesonal.PhoneBE.service.Auth.KakaoService;
import Phonesonal.PhoneBE.web.dto.Auth.KakaoRequestDTO;
import Phonesonal.PhoneBE.web.dto.Auth.LoginResultDTO;
import Phonesonal.PhoneBE.web.dto.Auth.SignupRequestDTO;
import Phonesonal.PhoneBE.web.dto.InfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final KakaoService kakaoService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/kakao/login")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        // 프론트엔드로 code 전달하면서 리다이렉트
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000/auth/callback?code=" + code))
                .build();
    }

    @PostMapping("/kakao/login")
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
            String tempToken = jwtTokenProvider.createTempToken(kakaoEmail, kakaoUserInfo);
            LoginResultDTO response = LoginResultDTO.builder()
                    .tempToken(tempToken)
                    .isNewUser(true)
                    .build();
            return ApiResponse.onSuccess(response);
        }
    }

    @PostMapping("/signup")
    public ApiResponse<LoginResultDTO> signup(@RequestBody SignupRequestDTO request) {
        System.out.println("=== Authorization Header ===");
        System.out.println("Received tempToken: " + request.getTempToken());
        System.out.println("===========================");
        try {
            // 임시 토큰에서 카카오 정보 추출
            Map<String, Object> kakaoUserInfo = jwtTokenProvider.getKakaoInfoFromTempToken(request.getTempToken());
            String kakaoName = kakaoUserInfo.get("nickname").toString();
            String kakaoEmail = kakaoUserInfo.get("email").toString();

            // 이미 가입된 유저인지 재확인
            if (userRepository.findByEmail(kakaoEmail).isPresent()) {
                return ApiResponse.onFailure("USER_ALREADY_EXISTS", "이미 가입된 사용자입니다", null);
            }

            // 새 유저 생성
            User newUser = User.builder()
                    .name(kakaoName)
                    .email(kakaoEmail)
                    .nickname(request.getNickname())
                    .age(request.getAge())
                    .gender(request.getGender())
                    .deadline(request.getDeadline())
                    .height(request.getHeight())
                    .weight(request.getWeight())
                    .socialType(SocialType.KAKAO)
                    .created_at(LocalDateTime.now())
                    // 카카오에서 받은 다른 정보들도 필요시 추가
                    .build();

            userRepository.save(newUser);

            // 정식 JWT 토큰 발급
            String jwtAccessToken = jwtTokenProvider.createToken(kakaoEmail);
            String jwtRefreshToken = jwtTokenProvider.createRefreshToken(kakaoEmail);

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