package Phonesonal.PhoneBE.web.dto.Food;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserMealResponseDTO {
    private Long recordId;
    private Long foodId;
    private String foodName;
    private MealTime mealTime;
    private LocalDate date;
    private boolean isCustom;
    private Float carb;
    private Float protein;
    private Float fat;
    private Float calorie;
    private Float quantity;
    private String displayedServingSize; // 실제 프론트에 보여줄 값 (dynamic)
    private String defaultServingSize;  // DB에 저장된 디폴트 servingSize
    private String imageUrl;

}