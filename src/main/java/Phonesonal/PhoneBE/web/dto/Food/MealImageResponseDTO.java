package Phonesonal.PhoneBE.web.dto.Food;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealImageResponseDTO {
    private Long id;
    private LocalDate date;
    private MealTime mealTime;
    private String imageUrl;
}
