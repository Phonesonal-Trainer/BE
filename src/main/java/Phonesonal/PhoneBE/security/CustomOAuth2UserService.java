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

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 카카오에서 받아온 사용자 정보 처리
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        // 카카오에서 받아온 정보 추출
        Map<String, Object> attributes = oauth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String name = (String) profile.get("nickname");

        // DB에서 사용자 찾기 또는 새로 생성
        User user = userRepository.findByEmailAndSocialType(email, SocialType.KAKAO)
                .orElseGet(() -> createNewUser(email, name));

        return oauth2User;
    }

    private User createNewUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .socialType(SocialType.KAKAO)
                .build();
        return userRepository.save(user);
    }
}