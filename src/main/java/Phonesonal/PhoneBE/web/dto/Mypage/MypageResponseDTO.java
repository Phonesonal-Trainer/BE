package Phonesonal.PhoneBE.web.dto.Mypage;

import Phonesonal.PhoneBE.domain.enums.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class MypageResponseDTO {
    @Getter
    @Setter
    @Builder
    public static class HomeResponse {
        private String nickname;
        private long togetherWeeks;
        private int targetWeeks;
        private BigDecimal weight;
        private BigDecimal bodyFatRate;
        private BigDecimal BMI;
        private String muscleMass;
    }

    @Getter
    @Setter
    @Builder
    public static class ProfileResponse {
        private String nickname;
        private int age;
        private Gender gender;
        private BigDecimal height;
    }
}
