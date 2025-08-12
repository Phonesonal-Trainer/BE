package Phonesonal.PhoneBE.web.dto.Food;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToggleFavoriteResponseDTO {
    private Long foodId;
    private boolean isFavorite; // 변경 후 최종 상태
}