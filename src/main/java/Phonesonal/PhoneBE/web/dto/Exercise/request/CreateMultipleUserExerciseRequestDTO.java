package Phonesonal.PhoneBE.web.dto.Exercise.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateMultipleUserExerciseRequestDTO {
    private List<Long> exerciseIds;
}
