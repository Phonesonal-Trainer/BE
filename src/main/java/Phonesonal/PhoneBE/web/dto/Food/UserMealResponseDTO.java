package Phonesonal.PhoneBE.web.dto.Food;
import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserMealResponseDTO {
    private Long recordId;
    private String foodName;
    private MealTime mealTime;
    private LocalDate date;
    private Float quantity;

    //조화용으로 필드 추가
    private Long foodId;
    private boolean isCustom;
    private Float calorie;
    private String imageUrl;

    private String displayedServingSize; // 실제 프론트에 보여줄 값 (dynamic)
    private String defaultServingSize;  // DB에 저장된 디폴트 servingSize

}