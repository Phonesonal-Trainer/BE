package Phonesonal.PhoneBE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
//    엔티티 생성 완료 후 주석 제거
//    private final CustomOAuth2UserService customOAuth2UserService;
//    private final OAuth2AuthenticationSuccessHandler successHandler;
//
//    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
//                          OAuth2AuthenticationSuccessHandler successHandler) {
//        this.customOAuth2UserService = customOAuth2UserService;
//        this.successHandler = successHandler;
//    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**", "/oauth2/**").permitAll() //요청 api 추가 필요
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .userInfoEndpoint(userInfo ->
//                                userInfo.userService(customOAuth2UserService)
//                        )
//                        .successHandler(successHandler)
//                );
//
//        return http.build();
//    }
}
