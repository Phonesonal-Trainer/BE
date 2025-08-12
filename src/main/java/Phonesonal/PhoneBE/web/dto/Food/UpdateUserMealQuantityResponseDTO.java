package Phonesonal.PhoneBE.web.dto.Food;

// 파일: web/dto/Food/UpdateQuantityResponseDTO.java
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserMealQuantityResponseDTO {
    private Long recordId;
    private Float quantity;              // 수정된 양
    private String displayedServingSize; // "150g" 같은 표시용
}