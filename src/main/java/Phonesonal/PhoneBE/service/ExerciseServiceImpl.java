package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.GeneralException;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.exercise.DailyCalorie;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import Phonesonal.PhoneBE.domain.enums.exercise.ExerciseType;
import Phonesonal.PhoneBE.domain.enums.exercise.State;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.repository.*;
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
    private final DailyCalorieRepository dailyCalorieRepository;

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

    @Override
    public UserExerciseResponseDTO startUserExercise(Long userId, Long userExerciseId) {
        // 1. UserExercise 조회
        UserExercise userExercise = userExerciseRepository.findById(userExerciseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_EXERCISE_NOT_FOUND));

        // 2. 해당 운동이 요청한 사용자의 것인지 확인
        if(!userExercise.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 3. 이미 시작된 운동인지 확인
        if (userExercise.getState() == State.pending) {
            throw new GeneralException(ErrorStatus.USER_EXERCISE_ALREADY_STARTED);
        }

        // 4. 완료된 운동인지 확인
        if (userExercise.getState() == State.completed) {
            throw new GeneralException(ErrorStatus.USER_EXERCISE_ALREADY_COMPLETED);
        }

        // 5. 운동 상태를 진행 중으로 변경
        userExercise.setState(State.inProgress);

        UserExercise savedUserExercise = userExerciseRepository.save(userExercise);

        return convertToUserExerciseResponseDTO(savedUserExercise);
    }

    @Override
    public UserExerciseResponseDTO completeUserExercise(Long userId, Long userExerciseId) {
        // 1. UserExercise 조회
        UserExercise userExercise = userExerciseRepository.findById(userExerciseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_EXERCISE_NOT_FOUND));

        // 2. 해당 운동이 요청한 사용자의 것인지 확인
        if(!userExercise.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 3. 완료된 운동인지 확인
        if (userExercise.getState() == State.completed) {
            throw new GeneralException(ErrorStatus.USER_EXERCISE_ALREADY_COMPLETED);
        }

        // 4. 시작이 안된 운동인지 확인
        if (userExercise.getState() != State.pending) {
            throw new GeneralException(ErrorStatus.USER_EXERCISE_NOT_STARTED);
        }

        // 5. 운동 상태를 진행 중으로 변경
        userExercise.setState(State.completed);

        UserExercise savedUserExercise = userExerciseRepository.save(userExercise);

        // 6. 칼로리 계산 및 업데이트
        updateDailyCalories(userExercise);

        return convertToUserExerciseResponseDTO(savedUserExercise);
    }

    // 칼로리 계산 및 업데이트 메서드
    private void updateDailyCalories(UserExercise userExercise) {
        // 총 소모 칼로리 계산: 1회 칼로리 × 횟수 × 세트 수
        Exercise exercise = userExercise.getExercise();
        Integer totalCalories = exercise.getKcal() * userExercise.getCount() * userExercise.getSetCount();

        // 해당 날짜의 DailyCalorie 조회 또는 생성
        User user = userExercise.getUser();
        LocalDate exerciseDate = userExercise.getExerciseDate();

        DailyCalorie dailyCalorie = dailyCalorieRepository.findByUserAndDate(user, exerciseDate)
                .orElse(DailyCalorie.builder()
                        .user(user)
                        .date(exerciseDate)
                        .totalCalories(0)
                        .createdAt(LocalDateTime.now())
                        .build());

        // 칼로리 누적
        dailyCalorie.setTotalCalories(dailyCalorie.getTotalCalories() + totalCalories);
        dailyCalorie.setUpdatedAt(LocalDateTime.now());

        dailyCalorieRepository.save(dailyCalorie);
    }
}
