package Phonesonal.PhoneBE.web.dto.Exercise.response;

import Phonesonal.PhoneBE.domain.common.exercise.BodyPart;
import Phonesonal.PhoneBE.domain.common.exercise.ExerciseDescription;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExerciseDetailResponseDTO {
    private Long exerciseId; // 운동 ID
    private String name; // 운동 이름
    private String imageUrl; // 운동 이미지 URL
    private String youtubeUrl; // 유튜브 URL
    private String caution; // 주의사항
    private List<BodyPart> bodyPart; // 운동 부위
    private List<ExerciseDescriptionDTO> descriptions;

    @Getter
    @Builder
    public static class ExerciseDescriptionDTO {
        private Integer step; // 단계
        private String main; // 주요 설명
        private String sub; // 보조 설명
    }
}
