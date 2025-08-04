package Phonesonal.PhoneBE.web.dto.Food;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchFoodResponseDTO {
    private Long foodId;
    private String name;
    private String servingSize; //1인분
    // private Float quantity; //실제 양
    private Float calorie;
    private Float carb;
    private Float protein;
    private Float fat;
    private String imageUrl;
    private boolean isFavorite; // 즐겨찾기 여부
}
