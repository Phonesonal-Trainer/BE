package Phonesonal.PhoneBE.security;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.enums.SocialType;
import Phonesonal.PhoneBE.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public CustomOAuth2UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 카카오에서 받아온 사용자 정보 처리
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("kakao".equals(registrationId)) {
            return processKakaoUser(oauth2User);
        } else if ("google".equals(registrationId)) {
            return processGoogleUser(oauth2User);
        }

        return oauth2User;
    }

    private OAuth2User processKakaoUser(OAuth2User oauth2User) {
        // 기존 카카오 로직
        Map<String, Object> attributes = oauth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String name = (String) profile.get("nickname");

        User user = userRepository.findByEmailAndSocialType(email, SocialType.KAKAO)
                .orElseGet(() -> createNewUser(email, name, SocialType.KAKAO));

        return oauth2User;
    }

    private OAuth2User processGoogleUser(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        Optional<User> existingUser = userRepository.findByEmailAndSocialType(email, SocialType.GOOGLE);

        if (existingUser.isEmpty()) {
            // 신규 유저 - 임시 토큰 생성해서 세션에 저장하고 회원가입 페이지로
            String tempToken = jwtTokenProvider.createTempToken(email, attributes, SocialType.GOOGLE);
            // 세션이나 리다이렉트 파라미터로 tempToken 전달
            return oauth2User;
        } else {
            // 기존 유저 - JWT 생성해서 로그인 완료 처리
            return oauth2User;
        }
    }

    private User createNewUser(String email, String name, SocialType socialType) {
        User user = User.builder()
                .email(email)
                .name(name)
                .socialType(socialType)
                .build();
        return userRepository.save(user);
    }
}