package Phonesonal.PhoneBE.web.dto.Exercise.request;

import Phonesonal.PhoneBE.domain.enums.exercise.BodyCategory;
import Phonesonal.PhoneBE.domain.enums.exercise.ExerciseType;
import Phonesonal.PhoneBE.domain.enums.exercise.Weekday;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Builder
public class CreateUserExerciseRequestDTO { //유저가 직접 운동 만들기
    // Exercise 생성을 위한 정보
    @NotBlank(message = "운동명은 필수입니다")
    @Size(max = 20, message = "운동명은 20자 이하여야 합니다")
    private String name;

    @NotNull(message = "운동 타입은 필수입니다")
    private ExerciseType type; // aerobic, anaerobic, etc

    @NotNull(message = "1회당 칼로리는 필수입니다")
    @Min(value = 1, message = "칼로리는 1 이상이어야 합니다")
    private Integer kcal;

    // 무산소 운동인 경우 운동 부위 (유산소인 경우 null 가능)
    private BodyCategory bodyCategory; // chest, legs, back, arms, shoulder, Abdominal

    // UserExercise 생성을 위한 정보
    @NotNull(message = "횟수는 필수입니다")
    @Min(value = 1, message = "횟수는 1 이상이어야 합니다")
    private Integer count;

    @Min(value = 0, message = "무게는 0 이상이어야 합니다")
    private Integer weight; // 유산소의 경우 null 가능

    @NotNull(message = "세트 수는 필수입니다")
    @Min(value = 1, message = "세트 수는 1 이상이어야 합니다")
    private Integer setCount;

    @NotNull(message = "운동 날짜는 필수입니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate exerciseDate;
}

