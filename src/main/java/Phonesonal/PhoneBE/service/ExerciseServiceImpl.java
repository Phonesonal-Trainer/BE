package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.GeneralException;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.exercise.BodyPart;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import Phonesonal.PhoneBE.domain.enums.exercise.ExerciseType;
import Phonesonal.PhoneBE.domain.enums.exercise.State;
import Phonesonal.PhoneBE.domain.mapping.ExerciseBodyPart;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.repository.BodyPartRepository;
import Phonesonal.PhoneBE.repository.ExerciseRepository;
import Phonesonal.PhoneBE.repository.UserExerciseRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.web.dto.Exercise.request.CreateUserExerciseRequestDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.ExerciseDetailResponseDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.ExerciseResponseDTO;
import Phonesonal.PhoneBE.web.dto.Exercise.response.UserExerciseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final BodyPartRepository bodyPartRepository;

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
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 주차 계산 (계정 생성일로부터 몇 주차인지)
        LocalDateTime userCreatedAt = user.getCreated_at();
        LocalDate userCreatedDate = userCreatedAt.toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(userCreatedDate, request.getExerciseDate());
        int weekNumber = (int) (daysBetween / 7) + 1; // 1주차부터 시작

        // 3. 무산소 운동인데 운동 부위를 선택하지 않은 경우 예외 처리
        if (request.getType() == ExerciseType.anaerobic && request.getBodyCategory() == null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        // 4. 커스텀 Exercise 생성
        Exercise customExercise = Exercise.builder()
                .name(request.getName())
                .type(request.getType())
                .kcal(request.getKcal())
                .isCustom(true)
                .createdByUserId(userId)
                .build();

        Exercise savedExercise = exerciseRepository.save(customExercise);

        // 5. UserExercise 생성
        UserExercise userExercise = UserExercise.builder()
                .user(user)
                .exercise(savedExercise)
                .count(request.getCount())
                .weight(request.getWeight())
                .setCount(request.getSetCount())
                .exerciseDate(request.getExerciseDate())
                .weekNumber(weekNumber)
                .state(State.pending)
                .bookmark(false)
                .build();

        return convertToUserExerciseResponseDTO(userExerciseRepository.save(userExercise));
    }
}
