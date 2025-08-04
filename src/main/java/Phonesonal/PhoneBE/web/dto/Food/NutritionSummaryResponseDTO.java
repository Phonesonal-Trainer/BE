package Phonesonal.PhoneBE.web.dto.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
public class NutritionSummaryResponseDTO {
    private LocalDate date;
    private Map<MealTime, NutritionData> summary;
}