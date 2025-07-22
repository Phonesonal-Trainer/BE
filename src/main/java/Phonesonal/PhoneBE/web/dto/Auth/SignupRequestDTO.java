package Phonesonal.PhoneBE.web.dto.Auth;

import Phonesonal.PhoneBE.domain.enums.Gender;
import lombok.Getter;

@Getter
public class SignupRequestDTO {
    private String tempToken;
    private String nickname;
    private int age;
    private Gender gender;
    private int deadline;
    private int height;
    private int weight;
    //private int jibang;
    //private int muscle;
}
