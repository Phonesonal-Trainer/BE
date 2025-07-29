package Phonesonal.PhoneBE.web.dto.Auth;

import Phonesonal.PhoneBE.domain.enums.Gender;
import Phonesonal.PhoneBE.domain.enums.Purpose;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SignupRequestDTO {
    private String tempToken;
    private String nickname;
    private int age;
    private Gender gender;
    private Purpose purpose;
    private int deadline;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bodyFatRate;
    private BigDecimal muscleMass;
}
