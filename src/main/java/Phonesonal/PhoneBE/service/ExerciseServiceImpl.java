package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.GeneralException;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.repository.ExerciseRepository;
import Phonesonal.PhoneBE.repository.UserExerciseRepository;
import Phonesonal.PhoneBE.web.dto.Exercise.request.CreateUserExerciseRequestDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.ExerciseDetailResponseDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.ExerciseResponseDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.UserExerciseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final UserExerciseRepository userExerciseRepository;

    // exerciseId로 운동을 찾는 메서드
    private Exercise findExerciseById(Long exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EXERCISE_NOT_FOUND));
    }

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
        Exercise exercise = findExerciseById(exerciseId);

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

    @Override
    public List<UserExerciseResponseDTO> getMyExercisesList(Long userId, LocalDate exerciseDate) {
        List<UserExercise> userExercises = userExerciseRepository.findByUserIdAndExerciseDate(userId, exerciseDate);

        return userExercises.stream()
                .map(this::convertToUserExerciseResponseDTO)
                .collect(Collectors.toList());
    }

    private UserExerciseResponseDTO convertToUserExerciseResponseDTO(UserExercise ue) {
        return UserExerciseResponseDTO.builder()
                .userExerciseId(ue.getId())
                .exerciseId(ue.getExercise().getId())
                .name(ue.getExercise().getName())
                //.type(ue.getExercise().getType())
                .count(ue.getCount())
                .weight(ue.getWeight())
                .sets(ue.getSetCount())
                .weekNumber(ue.getWeekNumber())
                .date(ue.getExerciseDate().toString())
                .build();
    }

    @Override
    public UserExerciseResponseDTO createUserExercise(CreateUserExerciseRequestDTO request, Long userId) {
        // 유저 운동 생성 로직

        UserExercise userExercise = UserExercise.builder()
                .userId(userId)
                //.exercise(exercise)
                .count(request.getCount())
                .weight(request.getWeight())
                .set(request.getSets())
                .weekNumber(request.getWeekNumber())
                .date(LocalDate.parse(request.getDate()))
                .build();

        UserExercise savedUserExercise = userExerciseRepository.save(userExercise);

        return convertToUserExerciseResponseDTO(savedUserExercise);
    }
}
