package Phonesonal.PhoneBE.web.dto.Exercise.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyExerciseDTO {

    private DailyCalories dailyCalories;
    private List<UserExerciseResponseDTO> exercises;

    @Getter
    @Builder
    public static class DailyCalories {
        private Integer targetCalories;    // 하루 목표 사용 칼로리
        private Integer currentCalories;   // 현재 사용한 칼로리
    }
}
