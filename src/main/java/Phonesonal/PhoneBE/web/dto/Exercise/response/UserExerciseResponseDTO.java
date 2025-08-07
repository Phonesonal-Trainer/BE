package Phonesonal.PhoneBE.web.dto.Exercise.response;

import Phonesonal.PhoneBE.domain.enums.exercise.Weekday;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserExerciseResponseDTO {
    private Long userExerciseId; // 사용자 운동 기록 ID
    private String recordType; // "PLANNED" 또는 "CUSTOM"

    // 공통 필드
    private String exerciseName; // 운동 이름 (계획된 운동의 name 또는 직접 입력한 이름)
    private String date; // 운동 날짜 (YYYY-MM-DD 형식)
    private String state; // 운동 상태
    private String exerciseType; // 운동 타입 (anaerobic, aerobic, etc)
    private Integer actualMinutes; // 실제 운동 시간 (분 단위)

    // 계획된 운동용 필드 (recordType이 "PLANNED"일 때만 값 존재)
    private Long exerciseId; // 운동 ID
    private List<ExerciseSetDTO> exerciseSets;
//    private Integer count; // 반복 횟수
//    private Integer weight; // 중량
//    private Integer sets; // 세트 수

    // 직접 기록용 필드 (recordType이 "CUSTOM"일 때만 값 존재)
    private Integer caloriesBurned; // 소모 칼로리

    @Getter
    @Builder
    public static class ExerciseSetDTO {
        private Long setId; // 세트 ID
        private Integer setNumber;
        private Integer count; // 반복 횟수
        private Integer weight; // 중량
        private Boolean completed; // 완료 여부
    }
}
