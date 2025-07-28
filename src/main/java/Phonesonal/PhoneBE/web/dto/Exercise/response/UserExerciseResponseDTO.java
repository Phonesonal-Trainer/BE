package Phonesonal.PhoneBE.web.dto.Exercise.response;

import Phonesonal.PhoneBE.domain.enums.exercise.Weekday;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserExerciseResponseDTO {
    private Long userExerciseId; // 사용자 운동 기록 ID
    private Long exerciseId; // 운동 ID
    private String name; // 운동 이름
    private Integer count; // 반복 횟수
    private Integer weight; // 중량
    private Integer sets; // 세트 수
    private Integer weekNumber; // 주차
    private Weekday weekday; //요일
    private String date; // 운동 날짜 (YYYY-MM-DD 형식)
}
