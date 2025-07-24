package Phonesonal.PhoneBE.web.dto.Exercise;

import Phonesonal.PhoneBE.domain.common.exercise.BodyPart;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExerciseDetailResponseDTO {
    private Long exerciseId; // 운동 ID
    private String name; // 운동 이름
    private String description; // 운동 설명
    private String imageUrl; // 운동 이미지 URL
    private String youtubeUrl; // 유튜브 URL
    private List<BodyPart> bodyPart; // 운동 부위
}
