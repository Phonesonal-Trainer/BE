package Phonesonal.PhoneBE.service.Exercise;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.handler.CommonExceptionHandler;
import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import Phonesonal.PhoneBE.domain.enums.ExerciseFeedback;
import Phonesonal.PhoneBE.domain.enums.exercise.State;
import Phonesonal.PhoneBE.domain.mapping.ExerciseSet;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.repository.*;
import Phonesonal.PhoneBE.service.AI.GeminiExerciseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseRecommendationServiceImpl implements ExerciseRecommendationService {

    private final UserRepository userRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final ExerciseSetRepository exerciseSetRepository;
    private final GeminiExerciseService geminiExerciseService;
    private final FeedbackRepository feedbackRepository;
    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void generateInitialWeeklyRecommendation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Diagnosis diagnosis = user.getDiagnosis();
        if (diagnosis == null) {
            throw new CommonExceptionHandler(ErrorStatus.DIAGNOSIS_NOT_FOUND);
        }

        generateWeeklyRecommendation(user, null);
    }

    @Override
    @Transactional
    public void regenerateCurrentWeekRecommendation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Diagnosis diagnosis = user.getDiagnosis();
        if (diagnosis == null) {
            throw new CommonExceptionHandler(ErrorStatus.DIAGNOSIS_NOT_FOUND);
        }

        //현재 주차 범위 계산
        LocalDate thisMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate thisSunday = thisMonday.plusDays(6);

        // 현재 주차 운동 ID들을 수집
        List<UserExercise> currentExercises = userExerciseRepository
                .findByUserIdAndStartDateBetween(userId, thisMonday, thisSunday);

        List<Long> currentExerciseIds = currentExercises.stream()
                .map(ue -> ue.getExercise().getId())
                .distinct()
                .toList();

        // 기존 운동들 및 관련 세트들 삭제
        for (UserExercise userExercise : currentExercises) {
            List<ExerciseSet> sets = exerciseSetRepository.findByUserExerciseOrderBySetNumber(userExercise);
            exerciseSetRepository.deleteAll(sets);
        }
        userExerciseRepository.deleteAll(currentExercises);

        // 기존 운동 제외하고 재추천
        generateWeeklyRecommendation(user, currentExerciseIds);
    }

    @Override
    @Transactional
    public void generateWeeklyRecommendation(User user, List<Long> excludeExerciseIds) {
        try {
            Diagnosis diagnosis = user.getDiagnosis();
            List<Exercise> availableExercises = getAvailableExercises(excludeExerciseIds);

            // Gemini API 호출
            String geminiResponse = geminiExerciseService.generateWeeklyExerciseRecommendation(
                    user, diagnosis, availableExercises, excludeExerciseIds);

            // JSON 파싱
            Map<String, List<Map<String, Object>>> weeklyPlan = parseGeminiResponse(geminiResponse);

            // UserExercise 생성
            createUserExercisesFromPlan(user, weeklyPlan);

        } catch (Exception e) {
            log.error("운동 추천 생성 실패 userId: {}", user.getId(), e);
            throw new CommonExceptionHandler(ErrorStatus.EXERCISE_RECOMMENDATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void handleWeeklyFeedback(Long userId, ExerciseFeedback feedback, LocalDate weekStart) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        LocalDate weekEnd = weekStart.plusDays(6);
        List<UserExercise> weekExercises = userExerciseRepository
                .findByUserIdAndExerciseDateBetween(userId, weekStart, weekEnd);

        if (weekExercises.isEmpty()) {
            throw new CommonExceptionHandler(ErrorStatus.EXERCISE_WEEK_NOT_FOUND);
        }

        LocalDate nextWeekStart = weekStart.plusWeeks(1);

        switch (feedback) {
            //case SATISFIED -> copyToNextWeek(weekExercises, nextWeekStart);
            case HIGH -> {
                copyToNextWeek(weekExercises, nextWeekStart);
                adjustWeight(getNextWeekExercises(userId, nextWeekStart), feedback);
            }
            case LOW -> {
                copyToNextWeek(weekExercises, nextWeekStart);
                adjustWeight(getNextWeekExercises(userId, nextWeekStart), feedback);
            }
            case MANY -> {
                copyToNextWeek(weekExercises, nextWeekStart);
                increaseExerciseVariety(userId, nextWeekStart);
            }
            case FEW -> {
                copyToNextWeek(weekExercises, nextWeekStart);
                reduceExerciseVolume(getNextWeekExercises(userId, nextWeekStart));
            }
            case DISLIKE -> {
                // 배치에서 처리
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 1 * * MON") // 매주 월요일 자정에 실행
    @Transactional
    public void processWeeklyFeedbackBatch() {
        LocalDate lastSunday = LocalDate.now().minusDays(1);

        // 지난 주 일요일에 제출된 피드백들을 조회
        int lastWeek = calculateWeekNumber(lastSunday);

        //Dislike 피드백 받은 유저들 재추천
        List<Object[]> dislikeUsers = feedbackRepository
                .findUsersWithExerciseFeedback(lastWeek, ExerciseFeedback.DISLIKE);

        for (Object[] result : dislikeUsers) {
            Long userId = (Long) result[0];
            try {
                List<Long> excludeIds = getLastWeekExerciseIds(userId, lastSunday.minusDays(6));
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    generateWeeklyRecommendation(user, excludeIds);
                }
            } catch (Exception e) {
                log.warn("배치 재추천 실패 userId: {}", userId, e);
            }
        }

        // SATISFIED 피드백 받은 유저들 - 동일 운동 복사
        List<Object[]> satisfiedUsers = feedbackRepository
                .findUsersWithExerciseFeedback(lastWeek, ExerciseFeedback.SATISFIED);

        for (Object[] result : satisfiedUsers) {
            Long userId = (Long) result[0];
            try {
                LocalDate lastMonday = lastSunday.minusDays(6);
                LocalDate thisMonday = LocalDate.now();
                List<UserExercise> lastWeekExercises = userExerciseRepository
                        .findByUserIdAndExerciseDateBetween(userId, lastMonday, lastSunday);
                copyToNextWeek(lastWeekExercises, thisMonday);
            } catch (Exception e) {
                log.error("만족 피드백 처리 실패 userId: {}", userId, e);
            }
        }

        // HIGH/LOW 피드백 받은 유저들 - 중량 조정
        processIntensityFeedback(lastWeek, ExerciseFeedback.HIGH);
        processIntensityFeedback(lastWeek, ExerciseFeedback.LOW);

        //MANY 피드백 받은 유저들 -운동 종류 증가
        List<Object[]> manyUsers = feedbackRepository
                .findUsersWithExerciseFeedback(lastWeek, ExerciseFeedback.MANY);

        for (Object[] result : manyUsers) {
            Long userId = (Long) result[0];
            try {
                LocalDate thisMonday = LocalDate.now();
                copyLastWeekAndIncreaseVariety(userId, thisMonday);
            } catch (Exception e) {
                log.error("운동 종류 증가 실패 userId: {}", userId, e);
            }
        }

        // FEW 피드백 받은 유저들 - 운동량 감소
        List<Object[]> fewUsers = feedbackRepository
                .findUsersWithExerciseFeedback(lastWeek, ExerciseFeedback.FEW);

        for (Object[] result : fewUsers) {
            Long userId = (Long) result[0];
            try {
                LocalDate thisMonday = LocalDate.now();
                copyLastWeekAndReduceVolume(userId, thisMonday);
            } catch (Exception e) {
                log.error("운동량 감소 실패 userId: {}", userId, e);
            }
        }
    }

    // 다음 주로 운동 복사
    private void copyToNextWeek(List<UserExercise> currentWeekExercises, LocalDate nextWeekStart) {
        for (UserExercise current : currentWeekExercises) {
            int dayOffset = current.getExerciseDate().getDayOfWeek().getValue() - 1;
            LocalDate nextExerciseDate = nextWeekStart.plusDays(dayOffset);

            UserExercise nextWeekExercise = UserExercise.builder()
                    .user(current.getUser())
                    .exercise(current.getExercise())
                    .exerciseDate(nextExerciseDate)
                    .state(State.pending)
                    .goalPeriod(current.getGoalPeriod())
                    .bookmark(current.getBookmark())
                    .build();

            UserExercise savedUserExercise = userExerciseRepository.save(nextWeekExercise);

            // ExerciseSet 복사
            List<ExerciseSet> currentSets = exerciseSetRepository.findByUserExerciseOrderBySetNumber(current);
            for (ExerciseSet currentSet : currentSets) {
                ExerciseSet nextSet = ExerciseSet.builder()
                        .userExercise(savedUserExercise)
                        .setNumber(currentSet.getSetNumber())
                        .weight(currentSet.getWeight())
                        .reps(currentSet.getReps())
                        .completed(false)
                        .build();
                exerciseSetRepository.save(nextSet);
            }
        }
    }

    //무게 조절
    private void adjustWeight(List<UserExercise> exercises, ExerciseFeedback feedback) {
        for (UserExercise exercise : exercises) {
            List<ExerciseSet> sets = exerciseSetRepository.findByUserExerciseOrderBySetNumber(exercise);
            for(ExerciseSet set : sets) {
                int currentWeight = set.getWeight() != null ? set.getWeight() : 0;
                if(feedback == ExerciseFeedback.HIGH) {
                    set.setWeight(currentWeight + 2); // 일단 2로 설정
                } else if(feedback == ExerciseFeedback.LOW) {
                    set.setWeight(Math.max(0, currentWeight - 2));
                }
                exerciseSetRepository.save(set);
            }
        }
    }

    //운동 종류 증가
    private void increaseExerciseVariety(Long userId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        List<UserExercise> weekExercises = userExerciseRepository
                .findByUserIdAndExerciseDateBetween(userId, weekStart, weekEnd);

        for (UserExercise currentUE : weekExercises) {
            if (currentUE.getExercise().getBodyParts().isEmpty()) continue;

            String targetBodyCategory = currentUE.getExercise().getBodyParts().get(0)
                    .getBodyPart().getBodyCategory().name();

            List<Exercise> additionalExercises = exerciseRepository
                    .findByBodyCategoryExcludingExercise(targetBodyCategory, currentUE.getExercise().getId());

            if(!additionalExercises.isEmpty()) {
                Exercise additionalExercise = additionalExercises.get(0);

                UserExercise additionalUE = UserExercise.builder()
                        .user(currentUE.getUser())
                        .exercise(additionalExercise)
                        .exerciseDate(currentUE.getExerciseDate())
                        .state(State.pending)
                        .goalPeriod(currentUE.getGoalPeriod())
                        .bookmark(false)
                        .build();

                UserExercise savedAdditional = userExerciseRepository.save(additionalUE);
                createDefaultSets(savedAdditional, additionalExercise);
            }
        }
    }

    //운동량 감소
    private void reduceExerciseVolume(List<UserExercise> exercises) {
        for (UserExercise exercise : exercises) {
            List<ExerciseSet> sets = exerciseSetRepository.findByUserExerciseOrderBySetNumber(exercise);

            if(sets.size() >= 3){
                ExerciseSet lastSet = sets.get(sets.size() - 1);
                exerciseSetRepository.delete(lastSet);
            }
        }
    }

    //강도 피드백 처리
    private void processIntensityFeedback(int week, ExerciseFeedback feedback) {
        List<Object[]> users = feedbackRepository
                .findUsersWithExerciseFeedback(week,feedback);

        for(Object[] result : users){
            Long userId = (Long)result[0];
            try {
                LocalDate lastSunday = LocalDate.now().minusDays(1);
                LocalDate lastMonday = lastSunday.minusDays(6);
                LocalDate thisMonday = LocalDate.now();

                List<UserExercise> lastWeekExercises = userExerciseRepository
                        .findByUserIdAndExerciseDateBetween(userId, lastMonday, lastSunday);

                copyToNextWeek(lastWeekExercises, thisMonday);

                List<UserExercise> thisWeekExercises = userExerciseRepository
                        .findByUserIdAndExerciseDateBetween(userId, thisMonday, thisMonday.plusDays(6));

                adjustWeight(thisWeekExercises, feedback);
            }catch (Exception e) {
                log.error("강도 조절 실패 userId: {}, feedback: {}", userId, feedback, e);
            }
        }
    }

    //지난 주 복사 후 운동 종류 증가
    private  void copyLastWeekAndIncreaseVariety(Long userId, LocalDate thisMonday) {
        LocalDate lastSunday = LocalDate.now().minusDays(1);
        LocalDate lastMonday = lastSunday.minusDays(6);

        List<UserExercise> lastWeekExercises = userExerciseRepository
                .findByUserIdAndExerciseDateBetween(userId, lastMonday, lastSunday);

        copyToNextWeek(lastWeekExercises, thisMonday);
        increaseExerciseVariety(userId, thisMonday);
    }

    //지난 주 복사 후 운동량 감소
    private void copyLastWeekAndReduceVolume(Long userId, LocalDate thisMonday) {
        LocalDate lastSunday = LocalDate.now().minusDays(1);
        LocalDate lastMonday = lastSunday.minusDays(6);

        List<UserExercise> lastWeekExercises = userExerciseRepository
                .findByUserIdAndExerciseDateBetween(userId, lastMonday, lastSunday);

        copyToNextWeek(lastWeekExercises, thisMonday);

        List<UserExercise> thisWeekExercises = userExerciseRepository
                .findByUserIdAndExerciseDateBetween(userId, thisMonday, thisMonday.plusDays(6));

        reduceExerciseVolume(thisWeekExercises);
    }

    // 다음 주 운동들 조회
    private List<UserExercise> getNextWeekExercises(Long userId, LocalDate nextWeekStart) {
        LocalDate nextWeekEnd = nextWeekStart.plusDays(6);
        return userExerciseRepository.findByUserIdAndExerciseDateBetween(userId, nextWeekStart, nextWeekEnd);
    }

    //사용 가능한 운동 목록 조회
    private List<Exercise> getAvailableExercises(List<Long> excludeIds) {
        if (excludeIds == null || excludeIds.isEmpty()) {
            return exerciseRepository.findAll();
        }
        return exerciseRepository.findByIdNotIn(excludeIds);
    }

    // 지난 주 운동 ID들 조회
    private List<Long> getLastWeekExerciseIds(Long userId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        List<UserExercise> lastWeekExercises = userExerciseRepository
                .findByUserIdAndExerciseDateBetween(userId, weekStart, weekEnd);

        return lastWeekExercises.stream()
                .map(ue -> ue.getExercise().getId())
                .distinct()
                .toList();
    }

    //주차 계산
    private int calculateWeekNumber(LocalDate date) {
        // 실제 구현에서는 GoalPeriod의 startDate 기준으로 계산
        // 여기서는 간단히 연초 기준으로 계산
        return date.getDayOfYear() / 7 + 1;
    }

    //제미나이 응답 파싱
    private Map<String, List<Map<String, Object>>> parseGeminiResponse(String geminiResponse) {
        try {
            return objectMapper.readValue(geminiResponse, new TypeReference<Map<String, List<Map<String, Object>>>>() {});
        } catch (Exception e) {
            throw new CommonExceptionHandler(ErrorStatus.AI_RESPONSE_PARSE_FAILED);
        }
    }

    //계획에서 UserExercise 생성
    private void createUserExercisesFromPlan(User user, Map<String, List<Map<String, Object>>> weeklyPlan) {
        LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);

        for (int day = 1; day <= 7; day++) {
            String dayKey = "day" + day;
            List<Map<String, Object>> dailyExercises = weeklyPlan.get(dayKey);

            if (dailyExercises == null) continue;

            LocalDate exerciseDate = startDate.plusDays(day - 1);

            for (Map<String, Object> exerciseData : dailyExercises) {
                Long exerciseId = ((Number) exerciseData.get("exerciseId")).longValue();
                Integer sets = ((Number) exerciseData.get("sets")).intValue();
                Integer reps = ((Number) exerciseData.get("reps")).intValue();
                Integer weight = exerciseData.containsKey("weight") ?
                        ((Number) exerciseData.get("weight")).intValue() : null;

                Exercise exercise = exerciseRepository.findById(exerciseId)
                        .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.EXERCISE_NOT_FOUND));

                UserExercise userExercise = UserExercise.builder()
                        .user(user)
                        .exercise(exercise)
                        .exerciseDate(exerciseDate)
                        .state(State.pending)
                        .goalPeriod(user.getGoalPeriod())
                        .bookmark(false)
                        .build();

                UserExercise savedUserExercise = userExerciseRepository.save(userExercise);

                // ExerciseSet 생성
                for (int setNum = 1; setNum <= sets; setNum++) {
                    ExerciseSet exerciseSet = ExerciseSet.builder()
                            .userExercise(savedUserExercise)
                            .setNumber(setNum)
                            .reps(reps)
                            .weight(weight != null ? weight : exercise.getDefaultWeight())
                            .completed(false)
                            .build();
                    exerciseSetRepository.save(exerciseSet);
                }
            }
        }
    }

    /**
     * 기본 세트 생성
     */
    private void createDefaultSets(UserExercise userExercise, Exercise exercise) {
        int defaultSets = exercise.getDefaultSet() != null ? exercise.getDefaultSet() : 3;

        for (int setNum = 1; setNum <= defaultSets; setNum++) {
            ExerciseSet exerciseSet = ExerciseSet.builder()
                    .userExercise(userExercise)
                    .setNumber(setNum)
                    .reps(exercise.getDefaultCount())
                    .weight(exercise.getDefaultWeight())
                    .completed(false)
                    .build();
            exerciseSetRepository.save(exerciseSet);
        }
    }
}