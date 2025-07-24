package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.web.dto.Exercise.ExerciseDetailResponseDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.ExerciseResponseDTO;

import java.util.List;

public interface ExerciseService {
    List<ExerciseResponseDTO> getExercisesList(Long userId);
    ExerciseDetailResponseDTO getExerciseDetail(Long exerciseId);
    //List<ExerciseResponseDTO> getBookmarkedExerciseList(Long userId); // 북마크된 운동 목록 조회
}
