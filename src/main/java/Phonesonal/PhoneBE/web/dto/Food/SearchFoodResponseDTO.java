package Phonesonal.PhoneBE.web.dto.Food;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchFoodResponseDTO {
    private Long foodId;
    private String name;
    private Float quantity;
    private Float calorie;
    private Float carb;
    private Float protein;
    private Float fat;
}
