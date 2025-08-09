package Phonesonal.PhoneBE.security;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.enums.SocialType;
import Phonesonal.PhoneBE.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        Optional<User> existingUser = userRepository.findByEmailAndSocialType(email, SocialType.GOOGLE);

        Map<String, Object> result = new HashMap<>();

        if (existingUser.isPresent()) {
            // 기존 유저 - JWT 토큰 생성
            String accessToken = jwtTokenProvider.createToken(email);
            String refreshToken = jwtTokenProvider.createRefreshToken(email);

            result.put("isNewUser", false);
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
            result.put("user", existingUser.get());
        } else {
            // 신규 유저 - 임시 토큰 생성
            Map<String, Object> googleUserInfo = oauth2User.getAttributes();
            String tempToken = jwtTokenProvider.createTempToken(email, googleUserInfo, SocialType.GOOGLE);

            result.put("isNewUser", true);
            result.put("tempToken", tempToken);
        }

        // JSON으로 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}