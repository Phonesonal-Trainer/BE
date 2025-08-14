package Phonesonal.PhoneBE.web.dto.Food;

import Phonesonal.PhoneBE.domain.Food;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NutritionData {
    private float carb = 0;
    private float protein = 0;
    private float fat = 0;
    private float calorie = 0;

    private Long recordCount = 0L;      // 해당 식사시간의 UserMeal 개수(기록 여부 판단용)
    private String imageUrl;            // 최신 1장 URL (없으면 null)
    private MealStatus status = MealStatus.NONE; // NONE | NO_IMAGE | WITH_IMAGE

    public enum MealStatus {
        NONE, NO_IMAGE, WITH_IMAGE
    }

    public void add(Food food, Float actualQuantity) {
        if (food == null) return;

        Float baseQuantity = parseServingSizeToQuantity(food.getServingSize()); // "100g" → 100
        float ratio;

        // 단건 조회와 동일 규칙:
        // - 기준량 없거나 0 → ratio = 1.0
        // - 기준량 있고 quantity가 null → ratio = 1.0
        // - 둘 다 있으면 → quantity / 기준량
        if (baseQuantity == null || baseQuantity <= 0f) {
            ratio = 1.0f;
        } else if (actualQuantity == null) {
            ratio = 1.0f;
        } else {
            ratio = actualQuantity / baseQuantity;
        }

        this.carb    += safe(food.getCarb())    * ratio;
        this.protein += safe(food.getProtein()) * ratio;
        this.fat     += safe(food.getFat())     * ratio;
        this.calorie += safe(food.getCalorie()) * ratio;
    }

    private float safe(Float value) {
        return value != null ? value : 0f;
    }

    private Float parseServingSizeToQuantity(String servingSize) {
        if (servingSize == null) return null;
        try {
            return Float.parseFloat(servingSize.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
