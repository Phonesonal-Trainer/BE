package Phonesonal.PhoneBE.web.dto.Exercise;

import Phonesonal.PhoneBE.domain.common.exercise.BodyPart;
import Phonesonal.PhoneBE.domain.enums.exercise.ExerciseType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExerciseResponseDTO {
    private Long exerciseId; // 운동 ID
    private String name; // 운동 이름
    private ExerciseType type; // 운동 종류
    private List<BodyPart> bodyPart; // 운동 부위
    private Boolean Bookmarked; // 북마크 여부
}
