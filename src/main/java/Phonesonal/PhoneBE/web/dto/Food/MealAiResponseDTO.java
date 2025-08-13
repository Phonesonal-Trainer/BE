package Phonesonal.PhoneBE.web.dto.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealAiResponseDTO {
    // "day1".."day7" -> 각 day에 MealTime별 3개 아이템
    private Map<String, Map<MealTime, List<MealItem>>> dayMap;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MealItem {
        private Long foodId;
        private Integer grams;
        private Float calorie;
        private Float carb;
        private Float protein;
        private Float fat;
    }
}
