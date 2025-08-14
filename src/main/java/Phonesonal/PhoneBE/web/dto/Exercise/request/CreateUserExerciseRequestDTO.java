package Phonesonal.PhoneBE.web.dto.Exercise.request;

import Phonesonal.PhoneBE.domain.enums.exercise.BodyCategory;
import Phonesonal.PhoneBE.domain.enums.exercise.ExerciseType;
import Phonesonal.PhoneBE.domain.enums.exercise.Weekday;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserExerciseRequestDTO { //유저가 직접 운동 만들기
    // Exercise 생성을 위한 정보
    @NotBlank
    private String exerciseName;

    private Integer kcal; // 운동에 소모되는 칼로리

    private ExerciseType exerciseType; // 운동 종류 (예: 유산소, 무산소 )
}

