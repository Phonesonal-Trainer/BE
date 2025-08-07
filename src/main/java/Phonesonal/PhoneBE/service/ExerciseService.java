package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.web.dto.Exercise.request.CreateUserExerciseRequestDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.ExerciseDetailResponseDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.ExerciseResponseDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.UserExerciseResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseService {
    List<ExerciseResponseDTO> getExercisesList(Long userId);
    ExerciseDetailResponseDTO getExerciseDetail(Long exerciseId);
    //List<ExerciseResponseDTO> getBookmarkedExerciseList(Long userId); // 북마크된 운동 목록 조회
    List<UserExerciseResponseDTO> getMyExercisesList(Long userId, LocalDate exerciseDate);
    UserExerciseResponseDTO createUserExercise(Long exerciseId, Long userId);
    UserExerciseResponseDTO createCustomUserExercise(CreateUserExerciseRequestDTO request, Long userId);
    UserExerciseResponseDTO startUserExercise(Long userId, Long exerciseId);
    UserExerciseResponseDTO completeUserExercise(Long userId, Long exerciseId);
    //UserExerciseResponseDTO updateSetCount(Long userId, Long userExerciseId, Integer setCount);
    //UserExerciseResponseDTO updateCountPerSet(Long userId, Long userExerciseId, Integer countPerSet);
}
