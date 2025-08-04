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

    public void add(Food food, float actualQuantity) {
        if (food == null) return;

        Float baseQuantity = parseServingSizeToQuantity(food.getServingSize());  // "100g" → 100
        if (baseQuantity == null || baseQuantity == 0f) return;

        float ratio = actualQuantity / baseQuantity;

        this.carb += safe(food.getCarb()) * ratio;
        this.protein += safe(food.getProtein()) * ratio;
        this.fat += safe(food.getFat()) * ratio;
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
