package Phonesonal.PhoneBE.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}") // ms 단위 (예: 3600000 = 1시간)
    private long expiration;

    @Value("${jwt.refreshTokenValidity}")
    private long refreshTokenValidity;

    private Key key;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //토큰 생성
    public String createToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .claim("type", "access")
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //리프레시토큰 생성
    public String createRefreshToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidity);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .claim("type", "refresh")
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 액세스 토큰인지 검사
    public boolean isAccessToken(String token) {
        String type = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("type", String.class);
        return type.equals("access");
    }

    // 액세스 토큰 만료 검사
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 임시 토큰 생성 (5분 만료)
    public String createTempToken(String email, Map<String, Object> kakaoUserInfo) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + 300000); // 5분

        return Jwts.builder()
                .setSubject(email)
                .claim("type", "temp")
                .claim("kakaoUserInfo", kakaoUserInfo)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)  // key 사용
                .compact();
    }

    // 임시 토큰에서 카카오 정보 추출
    public Map<String, Object> getKakaoInfoFromTempToken(String token) {
        try {
            System.out.println("토큰 파싱 시작: " + token.substring(0, 20) + "...");

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("파싱 성공!");
            System.out.println("토큰 타입: " + claims.get("type"));

            if (!"temp".equals(claims.get("type"))) {
                throw new IllegalArgumentException("Invalid token type");
            }

            return (Map<String, Object>) claims.get("kakaoUserInfo");

        } catch (Exception e) {
            System.out.println("파싱 실패: " + e.getMessage());
            throw e;
        }
    }
}
