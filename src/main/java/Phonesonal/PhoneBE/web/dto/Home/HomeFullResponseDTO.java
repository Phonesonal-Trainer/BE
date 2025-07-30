package Phonesonal.PhoneBE.web.dto.Home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HomeFullResponseDTO {
    private HomeResultDTO.HomeMainDTO main;
    private HomeResultDTO.HomeExerciseDTO exercise;
    private HomeResultDTO.HomeMealPlanDTO meal;
}
