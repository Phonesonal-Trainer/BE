package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.GeneralException;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.WeeklyStamp;
import Phonesonal.PhoneBE.domain.common.exercise.DailyExerciseRecord;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final BodyPartRepository bodyPartRepository;
    private final DailyExerciseRecordRepository dailyExerciseRecordRepository;
    private final WeeklyStampRepository weeklyStampRepository;
    private static final Long CUSTOM_EXERCISE_ID = 999999L; // 커스텀 운동용 고정 ID

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
        if (ue.isCustomExercise()) {
            // 커스텀 운동인 경우
            return UserExerciseResponseDTO.builder()
                    .userExerciseId(ue.getId())
                    .recordType("CUSTOM")
                    .exerciseName(ue.getCustomExerciseName()) // 실제 운동 이름
                    .date(ue.getExerciseDate().toString())
                    .state(ue.getState().name())
                    .exerciseType(ue.getCustomExerciseType() != null ? ue.getCustomExerciseType().name() : "etc")
                    .caloriesBurned(ue.getCaloriesBurned())
                    .actualMinutes(ue.getActualMinutes())
                    .build();
        } else {
            // 일반 계획된 운동인 경우
            return UserExerciseResponseDTO.builder()
                    .userExerciseId(ue.getId())
                    .recordType("PLANNED")
                    .exerciseId(ue.getExercise().getId())
                    .exerciseName(ue.getExercise().getName())
                    .date(ue.getExerciseDate().toString())
                    .state(ue.getState().name())
                    .exerciseType(ue.getExercise().getType() != null ? ue.getExercise().getType().name() : "etc")
                    .count(ue.getCount())
                    .weight(ue.getWeight())
                    .sets(ue.getSetCount())
                    .actualMinutes(ue.getActualMinutes())
                    .build();
        }
    }

    @Override
    public UserExerciseResponseDTO createUserExercise(Long exerciseId, Long userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 운동 조회
        Exercise exercise = findExerciseById(exerciseId);

        // 3. UserExercise 생성
        UserExercise userExercise = UserExercise.builder()
                .user(user)
                .exercise(exercise)
                .count(exercise.getDefaultCount()) // 초기 횟수
                .weight(exercise.getDefaultWeight()) // 초기 중량
                .setCount(exercise.getDefaultSet()) // 초기 세트 수
                .state(State.pending) // 초기 상태: 진행 대기 중
                .exerciseDate(LocalDate.now()) // 오늘 날짜로 설정
                .bookmark(false) // 북마크 기본값 false
                .build();

        // 4. UserExercise 저장
        UserExercise savedUserExercise = userExerciseRepository.save(userExercise);

        // 5. UserExerciseResponseDTO로 변환하여 반환
        return convertToUserExerciseResponseDTO(savedUserExercise);
    }

    @Override
    public UserExerciseResponseDTO createCustomUserExercise(CreateUserExerciseRequestDTO request, Long userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 커스텀 운동용 Exercise 조회
        Exercise customExercise = exerciseRepository.findById(CUSTOM_EXERCISE_ID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EXERCISE_NOT_FOUND));


        UserExercise userExercise = UserExercise.builder()
                .user(user)
                .exercise(customExercise) // 999999 ID 사용
                .customExerciseName(request.getExerciseName()) // 실제 운동 이름
                .caloriesBurned(request.getKcal())
                .customExerciseType(convertToCustomType(request.getExerciseType()))
                .exerciseDate(LocalDate.now())
                .state(State.completed)
                .bookmark(false)
                .actualMinutes(15) // 커스텀 운동은 기본적으로 15분으로 설정
                .build();

        UserExercise savedExercise = userExerciseRepository.save(userExercise);
        return convertToUserExerciseResponseDTO(savedExercise);
    }

    private UserExercise.CustomExerciseType convertToCustomType(ExerciseType exerciseType) {
        if (exerciseType == null) return UserExercise.CustomExerciseType.etc;

        switch (exerciseType) {
            case anaerobic: return UserExercise.CustomExerciseType.anaerobic;
            case aerobic: return UserExercise.CustomExerciseType.aerobic;
            default: return UserExercise.CustomExerciseType.etc;
        }
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
        if (userExercise.getState() == State.inProgress) {
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
        if (userExercise.getState() != State.inProgress) {
            throw new GeneralException(ErrorStatus.USER_EXERCISE_NOT_STARTED);
        }

        // 5. 운동 시간 계산 및 상태 변경
        Integer actualMinutes = calculateExerciseMinutes(userExercise);
        userExercise.setActualMinutes(actualMinutes);
        userExercise.setState(State.completed);

        UserExercise savedUserExercise = userExerciseRepository.save(userExercise);

        // 6. 일일 운동 데이터 업데이트
        updateDailyExerciseRecord(savedUserExercise);

        // 7. 스탬프 체크 및 부여
        checkAndUpdateStamp(savedUserExercise.getUser().getId(), savedUserExercise.getExerciseDate());

        return convertToUserExerciseResponseDTO(savedUserExercise);
    }

    // 스탬프 체크 및 업데이트 메서드
    private void checkAndUpdateStamp(Long userId, LocalDate exerciseDate) {
        // 해당 날짜의 전체 운동 목록 조회
        List<UserExercise> todayExercises = userExerciseRepository.findByUserIdAndExerciseDate(userId, exerciseDate);

        // 완료된 운동 개수 계산
        long completedCount = todayExercises.stream()
                .filter(exercise -> exercise.getState() == State.completed)
                .count();

        // 오늘 운동의 총 개수
        int totalCount = todayExercises.size();

        // 달성률 계산 휴일의 경우 추후 고려
        if (totalCount > 0 && (completedCount + 100.0 / totalCount) >= 80){
            // 월요일로 주차 계산
            LocalDate weekStartDate = exerciseDate.with(DayOfWeek.MONDAY); // 예시로 월의 첫날로 설정

            //WeeklyStamp 조회 또는 생성
            WeeklyStamp weeklyStamp = weeklyStampRepository
                    .findByUserIdAndWeekStartDate(userId, weekStartDate)
                    .orElseGet(() -> {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
                        return WeeklyStamp.builder()
                                .user(user)
                                .weekStartDate(weekStartDate)
                                .build();
                    });

            // 해당 요일 스탬프 업데이트
            weeklyStamp.updateStamp(exerciseDate);

            // 저장
            weeklyStampRepository.save(weeklyStamp);
        }
    }

    // 운동 시간 계산 메서드
    private Integer calculateExerciseMinutes(UserExercise userExercise) {
        if (userExercise.isCustomExercise()) {
            // 커스텀 운동은 기본 시간 적용 (30분)
            return 30;
        } else {
            Exercise exercise = userExercise.getExercise();
            // 1회당 초수 × 횟수 × 세트 수 = 총 초수 → 분으로 변환
            int totalSeconds = exercise.getSecondsPerRep() * userExercise.getCount() * userExercise.getSetCount();
            return totalSeconds / 60; // 초를 분으로 변환 (소수점 버림)
        }
    }



    // 일일 운동 데이터 업데이트 메서드
    private void updateDailyExerciseRecord(UserExercise userExercise) {
        User user = userExercise.getUser();
        LocalDate exerciseDate = userExercise.getExerciseDate();

        // 해당 날짜의 DailyExerciseRecord 조회 또는 생성
        DailyExerciseRecord dailyRecord = dailyExerciseRecordRepository.findByUserAndDate(user, exerciseDate)
                .orElse(DailyExerciseRecord.builder()
                        .user(user)
                        .date(exerciseDate)
                        .totalCalories(0)
                        .anaerobicMinutes(0)
                        .aerobicMinutes(0)
                        .createdAt(LocalDateTime.now())
                        .build());

        // 칼로리 계산 및 누적
        Integer totalCalories = calculateCalories(userExercise);
        dailyRecord.setTotalCalories(dailyRecord.getTotalCalories() + totalCalories);

        // 운동 시간 누적 (분 단위)
        Integer exerciseMinutes = userExercise.getActualMinutes();
        if (exerciseMinutes != null && exerciseMinutes > 0) {
            if (isAnaerobicExercise(userExercise)) {
                dailyRecord.setAnaerobicMinutes(dailyRecord.getAnaerobicMinutes() + exerciseMinutes);
            } else if (isAerobicExercise(userExercise)) {
                dailyRecord.setAerobicMinutes(dailyRecord.getAerobicMinutes() + exerciseMinutes);
            }
        }

        dailyRecord.setUpdatedAt(LocalDateTime.now());
        dailyExerciseRecordRepository.save(dailyRecord);
    }

    // 칼로리 계산 메서드
    private Integer calculateCalories(UserExercise userExercise) {
        if (userExercise.isCustomExercise()) {
            // 커스텀 운동은 사용자가 입력한 칼로리 사용
            return userExercise.getCaloriesBurned() != null ? userExercise.getCaloriesBurned() : 0;
        } else {
            Exercise exercise = userExercise.getExercise();
            // 1회 칼로리 × 횟수 × 세트 수
            return exercise.getKcal() * userExercise.getCount() * userExercise.getSetCount();
        }
    }

    // 무산소 운동인지 확인
    private boolean isAnaerobicExercise(UserExercise userExercise) {
        if (userExercise.isCustomExercise()) {
            return userExercise.getCustomExerciseType() == UserExercise.CustomExerciseType.anaerobic;
        } else {
            return userExercise.getExercise().getType() == ExerciseType.anaerobic;
        }
    }

    // 유산소 운동인지 확인
    private boolean isAerobicExercise(UserExercise userExercise) {
        if (userExercise.isCustomExercise()) {
            return userExercise.getCustomExerciseType() == UserExercise.CustomExerciseType.aerobic;
        } else {
            return userExercise.getExercise().getType() == ExerciseType.aerobic;
        }
    }

    // 세트 수 업데이트 -> 피드백 기능 구현 후 추가 구현 예정
    @Override
    public UserExerciseResponseDTO updateSetCount(Long userId, Long userExerciseId, Integer setCount){
        // 1. UserExercise 조회
        UserExercise userExercise = userExerciseRepository.findById(userExerciseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_EXERCISE_NOT_FOUND));

        // 2. 해당 운동이 요청한 사용자의 것인지 확인
        if(!userExercise.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 3. 운동 상태가 완료된 경우 예외 처리
        if (userExercise.getState() == State.completed) {
            throw new GeneralException(ErrorStatus.USER_EXERCISE_ALREADY_COMPLETED);
        }

        // 4. 세트 수 업데이트
        userExercise.setSetCount(setCount);

        UserExercise savedUserExercise = userExerciseRepository.save(userExercise);

        return convertToUserExerciseResponseDTO(savedUserExercise);
    }

    // 횟수 업데이트 메서드 -> 피드백 기능 구현 후 추가 구현 예정
    @Override
    public UserExerciseResponseDTO updateCountPerSet(Long userId, Long userExerciseId, Integer countPerSet) {
        // 1. UserExercise 조회
        UserExercise userExercise = userExerciseRepository.findById(userExerciseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_EXERCISE_NOT_FOUND));

        // 2. 해당 운동이 요청한 사용자의 것인지 확인
        if(!userExercise.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 3. 운동 상태가 완료된 경우 예외 처리
        if (userExercise.getState() == State.completed) {
            throw new GeneralException(ErrorStatus.USER_EXERCISE_ALREADY_COMPLETED);
        }

        // 4. 횟수 업데이트
        userExercise.setCount(countPerSet);

        UserExercise savedUserExercise = userExerciseRepository.save(userExercise);

        return convertToUserExerciseResponseDTO(savedUserExercise);
    }
}
