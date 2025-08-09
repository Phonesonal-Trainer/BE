package Phonesonal.PhoneBE.service.Report;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.code.util.DateUtil;
import Phonesonal.PhoneBE.apiPayload.exception.handler.CommonExceptionHandler;
import Phonesonal.PhoneBE.domain.*;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.common.exercise.DailyExerciseRecord;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import Phonesonal.PhoneBE.domain.mapping.ExerciseSet;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.repository.*;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.web.dto.Report.ReportResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportQueryServiceImpl implements ReportQueryService {

    private final RecommendMealRepository recommendMealRepository;
    private final UserMealRepository userMealRepository;
    private final GoalPeriodRepository goalPeriodRepository;
    private final FeedbackRepository feedbackRepository;
    private final WeightRecordRepository weightRecordRepository;
    private final DailyExerciseRecordRepository dailyExerciseRecordRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final ExerciseSetRepository exerciseSetRepository;

    private Number convertFloat(float value) {
        return value % 1.0 == 0 ? (int) value : value;
    }

    private Number convertFloatOrNull(Float value) {
        return value == null ? null : convertFloat(value);
    }

    private Integer convertFloatToInteger(Float value) {
        return value == null ? null : Math.round(value); // or floor, trunc, etc.
    }

    private String formatWeightChange(float diff) {
        String sign = diff > 0 ? "+" : (diff < 0 ? "-" : "");
        float abs = Math.abs(diff);
        return String.format("%s%.1fkg", sign, abs);
    }

    // 도우미 메서드들
    private Map<DayOfWeek, Number> initNullMap() {
        Map<DayOfWeek, Number> map = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            map.put(day, null);
        }
        return map;
    }

    private Map<DayOfWeek, Boolean> initNullBoolMap() {
        Map<DayOfWeek, Boolean> map = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            map.put(day, null);
        }
        return map;
    }

    public ReportResponseDTO.ExerciseFeedbackDTO getWeeklyExerciseFeedback(Long userId, Long goalPeriodId, int week) {

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new RuntimeException("GoalPeriod not found"));

        LocalDate startDate = goalPeriod.getStartDate().plusWeeks(week - 1);
        LocalDate endDate = startDate.plusDays(6);

        Boolean feedbackExist = feedbackRepository.existsByUserIdAndGoalPeriod_IdAndWeek(userId, goalPeriodId, week);

        // 2. daily 기록 조회
        List<DailyExerciseRecord> dailyRecords = dailyExerciseRecordRepository
                .findAllByUserIdAndDateBetween(userId, startDate, endDate);

        Map<DayOfWeek, Integer> dailyCalories = new EnumMap<>(DayOfWeek.class);
        int totalConsumed = 0;

        for (DailyExerciseRecord record : dailyRecords) {
            DayOfWeek day = record.getDate().getDayOfWeek();
            Integer calories = record.getTotalCalories();
            dailyCalories.put(day, calories);
            totalConsumed += (calories != null ? calories : 0);
        }

        // 3. 기록 없는 요일은 null 세팅
        for (DayOfWeek day : DayOfWeek.values()) {
            dailyCalories.putIfAbsent(day, null);
        }

        // 4. 목표 칼로리 계산
        List<UserExercise> weeklyExercises = userExerciseRepository
                .findByUserIdAndGoalPeriodIdAndExerciseDateBetween(userId, goalPeriodId, startDate, endDate);

        int totalTarget = 0;
        for (UserExercise ue : weeklyExercises){
            if(!ue.isCustomExercise()){
                //세트별 목표 칼로리 계산
                List<ExerciseSet> sets = exerciseSetRepository.findByUserExerciseOrderBySetNumber(ue);
                Exercise exercise = ue.getExercise();

                if(exercise != null && exercise.getKcal() != null){
                    for (ExerciseSet set : sets){
                        if(set.getReps() != null){
                            totalTarget += set.getReps() * exercise.getKcal();
                        }
                    }
                }
            } else {
                // 커스텀 운동
                totalTarget += ue.getCaloriesBurned() != null ? ue.getCaloriesBurned() : 0;
            }
        }

        // 5. 일일 평균 소모 칼로리
        int average = totalConsumed / 7;

        return ReportResponseDTO.ExerciseFeedbackDTO.builder()
                .week(week)
                .weekStart(startDate)
                .weekEnd(endDate)
                .feedbackExist(feedbackExist)
                .dailyCalories(dailyCalories)
                .totalConsumedCalories(totalConsumed)
                .totalTargetCalories(totalTarget)
                .averageDailyCalories(average)
                .build();
    }



    @Override
    public ReportResponseDTO.WeightFeedbackDTO getWeeklyWeightFeedback(Long userId, Long goalPeriodId, int week) {
        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.INVALID_GOAL_PERIOD));

        LocalDate[] weekRange = DateUtil.getWeekDateRange(goalPeriod.getStartDate(), goalPeriod.getEndDate(), week);

        if (weekRange == null) {
            throw new CommonExceptionHandler(ErrorStatus.INVALID_WEEK);
        }

        LocalDate weekStart = weekRange[0];
        LocalDate weekEnd = weekRange[1];

        if (weekStart.isAfter(LocalDate.now())) {
            throw new CommonExceptionHandler(ErrorStatus.INVALID_WEEK);
        }

        Boolean feedbackExist = feedbackRepository.existsByUserIdAndGoalPeriod_IdAndWeek(userId, goalPeriodId, week);

        List<WeightRecord> records = weightRecordRepository
                .findByUserIdAndGoalPeriodIdAndRecordDateBetween(userId, goalPeriodId,
                        weekStart.atStartOfDay(), weekEnd.plusDays(1).atStartOfDay());

        if (records.isEmpty()) {
            return ReportResponseDTO.WeightFeedbackDTO.builder()
                    .week(week)
                    .weekStart(weekStart)
                    .weekEnd(weekEnd)
                    .feedbackExist(feedbackExist)
                    .dailyWeight(initNullMap())
                    .changeFromTargetWeight(null)
                    .changeFromInitialWeight(null)
                    .build();
        }

        Map<DayOfWeek, Float> dailyWeight = new EnumMap<>(DayOfWeek.class);
        float total = 0f;
        int count = 0;

        for (DayOfWeek day : DayOfWeek.values()) {
            dailyWeight.put(day, null);
        }

        for (WeightRecord record : records) {
            DayOfWeek day = record.getRecordDate().toLocalDate().getDayOfWeek();
            Float weight = record.getWeight().floatValue();

            dailyWeight.put(day, weight); // 동일 요일에 여러 기록 있다면 마지막 기록 기준
            total += weight;
            count++;
        }

        float average = total / count;

        // 비교 기준: 진단(targetWeight), 초기 몸무게(user.weight)
        User user = goalPeriod.getUser();
        Diagnosis diagnosis = user.getDiagnosis();
        BigDecimal initialWeight = user.getWeight();
        BigDecimal targetWeight = diagnosis != null ? diagnosis.getTargetWeight() : null;

        String changeFromTarget = null;
        String changeFromInitial = null;

        if (targetWeight != null) {
            float diff = average - targetWeight.floatValue();
            changeFromTarget = formatWeightChange(diff);
        }

        if (initialWeight != null) {
            float diff = average - initialWeight.floatValue();
            changeFromInitial = formatWeightChange(diff);
        }

        Map<DayOfWeek, Number> finalDailyWeight = new EnumMap<>(DayOfWeek.class);
        for (Map.Entry<DayOfWeek, Float> entry : dailyWeight.entrySet()) {
            finalDailyWeight.put(entry.getKey(), convertFloatToInteger(entry.getValue()));
        }

        return ReportResponseDTO.WeightFeedbackDTO.builder()
                .week(week)
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .feedbackExist(feedbackExist)
                .dailyWeight(finalDailyWeight)
                .changeFromTargetWeight(changeFromTarget)
                .changeFromInitialWeight(changeFromInitial)
                .build();
    }


    @Override
    public ReportResponseDTO.MealFeedbackDTO getWeeklyMealReport(Long userId, Long goalPeriodId, int week) {
        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.INVALID_GOAL_PERIOD));

        LocalDate[] weekRange = DateUtil.getWeekDateRange(goalPeriod.getStartDate(), goalPeriod.getEndDate(), week);

        if (weekRange == null) {
            throw new CommonExceptionHandler(ErrorStatus.INVALID_WEEK);
        }

        LocalDate weekStart = weekRange[0];
        LocalDate weekEnd = weekRange[1];

        if (weekStart.isAfter(LocalDate.now())) {
            throw new CommonExceptionHandler(ErrorStatus.INVALID_WEEK);
        }

        List<RecommendMeal> completedMeals = recommendMealRepository
                .findByUserIdAndGoalPeriodIdAndDateBetweenAndComplete(userId, goalPeriodId, weekStart, weekEnd, CompleteStatus.COMPLETE);

        List<RecommendMeal> allPlanMeals = recommendMealRepository
                .findByUserIdAndGoalPeriodIdAndDateBetween(userId, goalPeriodId, weekStart, weekEnd);

        List<UserMeal> userMeals = userMealRepository
                .findByUserIdAndGoalPeriodIdAndDateBetween(userId, goalPeriodId, weekStart, weekEnd);

        Boolean feedbackExist = feedbackRepository.existsByUserIdAndGoalPeriod_IdAndWeek(userId, goalPeriodId, week);

        // 아무 기록도 없는 경우 조기 반환
        boolean noRecords = completedMeals.isEmpty() && allPlanMeals.isEmpty() && userMeals.isEmpty();
        if (noRecords) {
            return ReportResponseDTO.MealFeedbackDTO.builder()
                    .week(week)
                    .weekStart(weekStart)
                    .weekEnd(weekEnd)
                    .feedbackExist(feedbackExist)
                    .totalConsumedCalories(null)
                    .totalTargetCalories(null)
                    .averageDailyCalories(null)
                    .dailyCalories(initNullMap())
                    .dailyStamps(initNullBoolMap())
                    .stampMessage(null)
                    .build();
        }

        // ↓ 정상 케이스 계산 ↓
        float totalConsumed = 0f;
        float totalTarget = 0f;

        Map<DayOfWeek, Float> dailyCalories = new EnumMap<>(DayOfWeek.class);
        Map<DayOfWeek, Float> dailyTargetCalories = new EnumMap<>(DayOfWeek.class);
        Map<DayOfWeek, Boolean> dailyStamps = new EnumMap<>(DayOfWeek.class);

        for (DayOfWeek day : DayOfWeek.values()) {
            dailyCalories.put(day, null);
            dailyTargetCalories.put(day, 0f);
        }

        for (RecommendMeal meal : completedMeals) {
            float cal = meal.getFood().getCalorie();
            totalConsumed += cal;
            dailyCalories.merge(meal.getDate().getDayOfWeek(), cal, (oldVal, newVal) ->
                    oldVal == null ? newVal : oldVal + newVal
            );
        }

        for (UserMeal meal : userMeals) {
            float cal = meal.getFood().getCalorie();
            totalConsumed += cal;
            dailyCalories.merge(meal.getDate().getDayOfWeek(), cal, (oldVal, newVal) ->
                    oldVal == null ? newVal : oldVal + newVal
            );
        }

        for (RecommendMeal meal : allPlanMeals) {
            float cal = meal.getFood().getCalorie();
            totalTarget += cal;
            dailyTargetCalories.merge(meal.getDate().getDayOfWeek(), cal, Float::sum);
        }

        long countOfRecordedDays = dailyCalories.values().stream()
                .filter(Objects::nonNull)
                .count();
        float average = countOfRecordedDays == 0 ? 0f : totalConsumed / countOfRecordedDays;

        int stampCount = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            Float consumed = dailyCalories.get(day);
            Float target = dailyTargetCalories.get(day);

            if (consumed == null || target == null || target == 0f) {
                dailyStamps.put(day, null);
            } else {
                float ratio = consumed / target;
                boolean isStamp = ratio >= 0.9f && ratio <= 1.1f;
                dailyStamps.put(day, isStamp);
                if (isStamp) stampCount++;
            }
        }

        System.out.println("weekend: " + weekEnd);
        System.out.println("now: " + LocalDate.now());
        System.out.println("stampcount: " + stampCount);

        String stampLevelMessage = null;
        if (countOfRecordedDays > 0) {
            if (weekEnd.isBefore(LocalDate.now())) {
                // 지난 주차 (주차 종료 후)
                if (stampCount <= 2) {
                    stampLevelMessage = "회원님, 목표를 향한 의지를 다시 불태워볼 시간이에요🔥";
                } else if (stampCount <= 5) {
                    stampLevelMessage = "회원님, 잘하고 계세요! 조금만 더 힘내볼까요?💪";
                } else {
                    stampLevelMessage = "회원님, 정말 최고예요! 이대로만 가봅시다💯";
                }
            } else {
                // 아직 진행 중인 주차
                stampLevelMessage = "제공된 루틴을 열심히 수행해 스탬프를 모아봅시다✔️";
            }
        }

        Map<DayOfWeek, Number> finalDailyCalories = new EnumMap<>(DayOfWeek.class);
        for (Map.Entry<DayOfWeek, Float> entry : dailyCalories.entrySet()) {
            finalDailyCalories.put(entry.getKey(), convertFloatToInteger(entry.getValue()));
        }

        return ReportResponseDTO.MealFeedbackDTO.builder()
                .week(week)
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .feedbackExist(feedbackExist)
                .totalConsumedCalories(convertFloat(totalConsumed))
                .totalTargetCalories(convertFloat(totalTarget))
                .averageDailyCalories(convertFloat(average))
                .dailyCalories(finalDailyCalories)
                .dailyStamps(dailyStamps)
                .stampMessage(stampLevelMessage)
                .build();
    }


}
