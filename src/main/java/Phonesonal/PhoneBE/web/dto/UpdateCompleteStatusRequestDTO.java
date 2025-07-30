package Phonesonal.PhoneBE.web.dto;

import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompleteStatusRequestDTO {
    private Long foodId;
    private Long goalPeriodId;
    private LocalDate date;
    private MealTime mealTime;
    private String complete;
}
