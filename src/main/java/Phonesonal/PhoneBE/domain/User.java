package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.enums.Gender;
import Phonesonal.PhoneBE.domain.enums.SocialType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //소셜 이메일
    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    //소셜로부터 받아온 이름
    private String name;

    //직접 사용자가 설정한 이름
    private String nickname;

    //소셜타입(카카오, 애플, 구글)
    private SocialType socialType;

    //성별
    private Gender gender;

    //키
    private int height;

    //무게
    private int weight;

    //나이
    private int age;

    //목표기간
    private int deadline;

    //계정 생성 시간
    private LocalDateTime created_at;
}
