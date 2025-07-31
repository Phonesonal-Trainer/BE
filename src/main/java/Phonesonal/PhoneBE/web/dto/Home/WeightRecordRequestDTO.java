package Phonesonal.PhoneBE.web.dto.Home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
public class WeightRecordRequestDTO {
    private BigDecimal weight;
    private LocalDate recordDate;
    private Long userId;
}
