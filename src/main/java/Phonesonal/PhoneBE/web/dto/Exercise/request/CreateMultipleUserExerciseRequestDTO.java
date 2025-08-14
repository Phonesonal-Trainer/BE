package Phonesonal.PhoneBE.web.dto.Exercise.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMultipleUserExerciseRequestDTO {
    private List<Long> exerciseIds;
}
