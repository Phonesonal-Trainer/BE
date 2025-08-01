package Phonesonal.PhoneBE.web.dto.Food;

import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CompleteStatusResponseDTO {
    private Long foodId;
    private CompleteStatus complete;
}

