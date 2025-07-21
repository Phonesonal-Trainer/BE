package Phonesonal.PhoneBE.web.dto;

import Phonesonal.PhoneBE.domain.RecommendMeal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class RecommendMealResponseDTO {

    private Long foodId;
    private String foodName;
    private String mealTime;
    private LocalDate date;
    private Float quantity;
    private String complete;

    public static RecommendMealResponseDTO from(RecommendMeal entity) {
        return RecommendMealResponseDTO.builder()
                .foodId(entity.getFood().getFoodId())
                .foodName(entity.getFood().getName())
                .mealTime(entity.getMealTime().name())
                .date(entity.getDate())
                .quantity(entity.getQuantity())
                .complete(entity.getComplete().name())
                .build();
    }
}

