package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.repository.ExerciseRepository;
import Phonesonal.PhoneBE.repository.UserExerciseRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Exercise.ExerciseDetailResponseDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.ExerciseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final UserExerciseRepository userExerciseRepository;

    @Override
    public List<ExerciseResponseDTO> getExercisesList(Long userId) {
        return exerciseRepository.findAll().stream()
                .map(exercise -> convertToExerciseResponseDTO(exercise, userId))
                .collect(Collectors.toList());
    }

//    public List<ExerciseResponseDTO> getBookmarkedExerciseList(Long userId) {
//        List<UserExercise> bookmarkedExercises = userExerciseRepository.findByUserIdAndBookmarkTrue(userId);
//
//        return bookmarkedExercises.stream()
//                .map(UserExercise::getExercise)
//                .map(exercise -> convertToExerciseResponseDTO(exercise, userId))
//                .collect(Collectors.toList());
//    }

    @Override
    public ExerciseDetailResponseDTO getExerciseDetail(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseId));

        return convertToExerciseDetailResponseDTO(exercise);
    }

    private ExerciseResponseDTO convertToExerciseResponseDTO(Exercise exercise, Long userId) {
        boolean isBookmarked = checkIfBookmarked(exercise.getId(), userId);

        return ExerciseResponseDTO.builder()
                .exerciseId(exercise.getId())
                .name(exercise.getName())
                .type(exercise.getType())
                .bodyPart(exercise.getBodyParts().stream()
                        .map(ebp -> ebp.getBodyPart())
                        .collect(Collectors.toList()))
                .Bookmarked(isBookmarked)
                .build();
    }

    private ExerciseDetailResponseDTO convertToExerciseDetailResponseDTO(Exercise exercise) {
        return ExerciseDetailResponseDTO.builder()
                .exerciseId(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .imageUrl(exercise.getImageUrl())
                .youtubeUrl(exercise.getYoutubeUrl())
                .bodyPart(exercise.getBodyParts().stream()
                        .map(ebp -> ebp.getBodyPart())
                        .collect(Collectors.toList()))
                .build();
    }

    // 북마크 여부 확인 메서드 - UserExercise 테이블에서 확인
    private boolean checkIfBookmarked(Long exerciseId, Long userId) {
        return userExerciseRepository.existsByUserIdAndExerciseIdAndBookmarkTrue(userId, exerciseId);
    }
}
