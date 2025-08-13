package Phonesonal.PhoneBE.web.dto.Food;


import Phonesonal.PhoneBE.domain.enums.MealTime;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateMealRequestDTO {
    // 7일 플랜이 시작되는 날짜
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
}
