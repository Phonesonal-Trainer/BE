package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.domain.*;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.common.exercise.DailyExerciseRecord;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;
import Phonesonal.PhoneBE.domain.mapping.ExerciseSet;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.repository.*;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.repository.UserMealRepository;
import Phonesonal.PhoneBE.web.dto.Home.HomeFullResponseDTO;
import Phonesonal.PhoneBE.web.dto.Home.HomeResultDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static Phonesonal.PhoneBE.apiPayload.code.util.DateUtil.calculateWeek;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl {

    private final UserRepository userRepository;
    private final RecommendMealRepository recommendMealRepository;
    private final UserMealRepository userMealRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final DailyExerciseRecordRepository dailyExerciseRecordRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final DiagnosisRepository diagnosesRepository;
    private final ExerciseSetRepository exerciseSetRepository;
    private static final Logger log = LoggerFactory.getLogger(HomeServiceImpl.class);

    //추천 운동 소모 칼로리
    @Transactional(readOnly = true)
    public int getRecommanedBurnedCaloriesOnDate(Long user, LocalDate date, Long goalPeriodId) {
        List<UserExercise> exercises = userExerciseRepository.findWithExerciseByUserIdAndDate(user, date, goalPeriodId);

        return exercises.stream()
                .filter(userExercise -> userExercise.getExercise() != null && !userExercise.getExercise().getId().equals(999999L)) // 커스텀 운동 제외
                .mapToInt(userExercise -> {
                    Exercise exercise = userExercise.getExercise();
                    if(exercise != null && exercise.getKcal() != null){
                        List<ExerciseSet> completedSets = exerciseSetRepository.findByUserExerciseOrderBySetNumber(userExercise)
                                .stream()
                                .filter(ExerciseSet::getCompleted)
                                .collect(Collectors.toList());

                        return completedSets.stream()
                                .mapToInt(set -> exercise.getKcal() * (set.getReps() != null ? set.getReps() : 0))
                                .sum();
                    }
                    return 0;
                })
                .sum();
    }
    //추천 섭취 칼로리
    public double getRecommendedCaloriesByDate(Long userId,LocalDate date, Long goalPeriodId) {
        List<RecommendMeal> recommandedMeals = recommendMealRepository.findWithFoodByUserIdAndDate(userId,date, goalPeriodId);

        double totalCalories = recommandedMeals.stream()
                .mapToDouble(recommendMeal -> recommendMeal.getFood().getCalorie() != null ? recommendMeal.getFood().getCalorie() : 0)
                .sum();

        return totalCalories;
    }

    //추천 단백질 합계
    public double getRecommendedProteinByDate(Long userId,LocalDate date, Long goalPeriodId) {
        List<RecommendMeal> recommandedMeals = recommendMealRepository.findWithFoodByUserIdAndDate(userId,date,goalPeriodId);

        double totalCalories = recommandedMeals.stream()
                .mapToDouble(recommendMeal -> recommendMeal.getFood().getProtein() != null ? recommendMeal.getFood().getProtein() : 0)
                .sum();

        return totalCalories;
    }

    //추천 탄수화물 합계
    public double getRecommendedCarbByDate(Long userId,LocalDate date, Long goalPeriodId) {
        List<RecommendMeal> recommandedMeals = recommendMealRepository.findWithFoodByUserIdAndDate(userId,date,goalPeriodId);

        double totalCalories = recommandedMeals.stream()
                .mapToDouble(recommendMeal -> recommendMeal.getFood().getCarb() != null ? recommendMeal.getFood().getCarb() : 0)
                .sum();

        return totalCalories;
    }
    //추천 지방 합계
    public double getRecommendedFatByDate(Long userId,LocalDate date, Long goalPeriodId) {
        List<RecommendMeal> recommandedMeals = recommendMealRepository.findWithFoodByUserIdAndDate(userId,date,goalPeriodId);

        double totalCalories = recommandedMeals.stream()
                .mapToDouble(recommendMeal -> recommendMeal.getFood().getFat() != null ? recommendMeal.getFood().getFat() : 0)
                .sum();

        return totalCalories;
    }

    //오늘 섭취한 칼로리 총량
    public double getTodayConsumedCaloriesByDate(Long userId,LocalDate date) {
        List<UserMeal> meals = userMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCalories = meals.stream()
                .mapToDouble(userMeal -> userMeal.getFood().getCalorie() != null ? userMeal.getFood().getCalorie() : 0)
                .sum();

        return totalCalories;
    }


    /*
    사용자가 오늘 섭취한 탄,단,지 각 총량

    public double getTodayConsumedProteinByDate(Long userId,LocalDate date) {
        List<UserMeal> meals = userMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalProtein = meals.stream()
                .mapToDouble(userMeal -> userMeal.getFood().getProtein() != null ? userMeal.getFood().getProtein() : 0)
                .sum();

        return totalProtein;
    }

    public double getTodayConsumedCarbByDate(Long userId,LocalDate date) {
        List<UserMeal> meals = userMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalCarb = meals.stream()
                .mapToDouble(userMeal -> userMeal.getFood().getCarb() != null ? userMeal.getFood().getCarb() : 0)
                .sum();

        return totalCarb;
    }

    public double getTodayConsumedFatByDate(Long userId,LocalDate date) {
        List<UserMeal> meals = userMealRepository.findWithFoodByUserIdAndDate(userId,date);

        double totalFat = meals.stream()
                .mapToDouble(userMeal -> userMeal.getFood().getCalorie() != null ? userMeal.getFood().getCalorie() : 0)
                .sum();

        return totalFat;
    }


    */
    //오늘 소비한 총 칼로리
    public int getTodayCaloriesBurnedByUser(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return dailyExerciseRecordRepository.findByUserAndDate(user, date)
                .map(DailyExerciseRecord::getTotalCalories)
                .orElse(0);
    }

    //식단 플랜 퍼센테이지
    public String HomeMealPercentageStatus(int percentage){

        String status;
        if(percentage == 0){
            status = "시작전";
        } else if (percentage < 90 || percentage > 25) {
            status = "부족";
        } else if (percentage > 90|| percentage < 110) {
            status = "적정";
        } else{
            status = "초과";
        }
        return status;
    }

    //운동플랜 퍼센테이지
    public String HomeExercisePercentageStatus(int percentage){

        String status;
        if(percentage == 0){
            status = "시작전";
        } else if (percentage < 100) {
            status = "부족";
        } else {
            status = "달성";
        }
        return status;
    }

    //추천 무산소 시간
    public int getTodayAnaerobicExerciseTimeByDate(Long userId) {
        int totalAnaerbic = 0;
        if(diagnosesRepository.existsByUserId(userId)){
            Optional<Diagnosis> diagnosis = diagnosesRepository.findByUserId(userId);

            totalAnaerbic = (diagnosis.get().getStrengthTrainingTime())/(diagnosis.get().getWorkoutFrequency());
            return totalAnaerbic;
        }
        return totalAnaerbic;
    }

    //추천 유산소 운동 시간
    public int getTodayAerobicExerciseTimeByDate(Long userId){
        int totalAerobicTime = 0;
        if(diagnosesRepository.existsByUserId(userId)){

            Optional<Diagnosis> diagnosis = diagnosesRepository.findByUserId(userId);
            //추천 유산소 시간
            totalAerobicTime = (diagnosis.get().getCardioMinutesPerWeek())/(diagnosis.get().getCardioDaysPerWeek());
            return totalAerobicTime;
        }
        return totalAerobicTime;
    }

    //요일별 코멘트 메소드
    public String todayComment(LocalDate date){
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        String comment = switch (dayOfWeek) {
            case MONDAY -> "벌써 월요일이네요! 이번 주도 힘차게 달려볼까요?🏃";
            case TUESDAY -> "회원님! 오늘도 목표에 한 걸음 더 다가가봅시다🔥";
            case WEDNESDAY -> "오늘 하루도 파이팅! 폰스널 트레이너는 언제나 회원님을 응원합니다🍀";
            case THURSDAY -> "회원님! 오늘도 폰스널 트레이너와 함께 달려볼까요?🏃";
            case FRIDAY -> "불금엔 역시 운동이죠! 오늘도 의지를 불태워봅시다🔥";
            case SATURDAY -> "주말에도 방심은 금물! 운동도 식단도 잊지 말기로 약속해요🤙";
            case SUNDAY -> "벌써 이번 주도 끝을 향해 가네요! 마지막까지 힘내봅시다💪";
        };
        return comment;
    }


    public HomeResultDTO.HomeMainDTO getHomeData(Long userId, Long goalPeriodId) {
        User user = userRepository.getReferenceById(userId);
        // 기존 진단이 있는지 확인
        Optional<Diagnosis> existingDiagnosis = diagnosisRepository.findByUserId(userId);
        LocalDate date = LocalDate.now();
        String koreanDay = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

        double recommendedCalories = getRecommendedCaloriesByDate(userId, date,goalPeriodId);//추천 섭취 칼로리
        int recommendedBurnedCalories = getRecommanedBurnedCaloriesOnDate(userId, date,goalPeriodId);//추천 소비 칼로리
        double todayConsumedCalories = getTodayConsumedCaloriesByDate(userId, date);// 오늘 섭취한 칼로리
        int todayBurnedCalories = getTodayCaloriesBurnedByUser(userId, date);//오늘 소비한 칼로리

        // 오늘 먹은 식단의 총 칼로리 - 오늘 운동한 총 소비 칼로리
        double todayCalories = todayConsumedCalories-todayBurnedCalories;
        //추천 식단의 총 칼로리 - 추천 운동의 총 소비 칼로리
        double targetCalories = recommendedCalories-recommendedBurnedCalories;

        BigDecimal targetWeight = existingDiagnosis
                .map(Diagnosis::getTargetWeight)
                .orElse(BigDecimal.ZERO); // 값이 없으면 0으로 기본 처리
        int presentWeek = calculateWeek(user.getCreated_at().toLocalDate(), date);//유저 생성시간 기준으로 구현
        String comment = todayComment(date);



        return HomeResultDTO.HomeMainDTO.builder()
                .userId(userId)
                .targetCalories(targetCalories)
                .todayCalories(todayCalories)
                .date(date)
                .koreanDate(koreanDay)
                .presentWeek(presentWeek)
                .targetWeight(targetWeight)
                .comment(comment)
                .build();
    }

    public HomeResultDTO.HomeExerciseDTO getHomeExercise(Long userId, Long goalPeriodId) {
        String focusedBodyPart = "하체"; // 더미 데이터 집중 부위
        int anaerobicExerciseTime =getTodayAnaerobicExerciseTimeByDate(userId); // 무산소 시간
        int aerobicExerciseTime = getTodayAerobicExerciseTimeByDate(userId); // 유산소 시간
        int todayBurnedCalories  = getTodayCaloriesBurnedByUser(userId, LocalDate.now());//오늘 칼로리 소비량
        int todayRecommanedBurnedCalories = getRecommanedBurnedCaloriesOnDate(userId, LocalDate.now(),goalPeriodId);//추천 칼로리 소비량
        int exercisePercentage = (todayBurnedCalories/todayRecommanedBurnedCalories)*100;//현재 byzero 문제 발생
        String exerciseStatus = HomeExercisePercentageStatus(exercisePercentage);

        return HomeResultDTO.HomeExerciseDTO.builder()
                .todayBurnedCalories(todayBurnedCalories)
                .todayRecommanedBurnedCalories(todayRecommanedBurnedCalories)
                .anaerobicExerciseTime(anaerobicExerciseTime)
                .aerobicExerciseTime(aerobicExerciseTime)
                .focusedBodyPart(focusedBodyPart)
                .exerciseStatus(exerciseStatus)
                .exercisePercentage(exercisePercentage)
                .build();
    }

    public HomeResultDTO.HomeMealPlanDTO getHomeMealPlan(Long userId, Long goalPeriodId) {
        LocalDate date = LocalDate.now();

        double recommendedCalories = getRecommendedCaloriesByDate(userId, date, goalPeriodId);//추천 칼로리 섭취량
        double calorie = getTodayConsumedCaloriesByDate(userId, date);//오늘 섭취한 총 칼로리
        double carb = getRecommendedCarbByDate(userId, date, goalPeriodId);//추천된 탄수화물 그램수
        double protein = getRecommendedProteinByDate(userId,date, goalPeriodId);//오늘 추천된 단백질 그램수
        double fat = getRecommendedFatByDate(userId,date, goalPeriodId);//오늘 추천된 지방 그램수
        int caloriePercentage = (int)(calorie/recommendedCalories)*100;
        String calorieStatus = HomeMealPercentageStatus(caloriePercentage);



        return HomeResultDTO.HomeMealPlanDTO.builder()
                .todayRecommendedCalories(recommendedCalories)
                .todayConsumedCalorie(calorie)
                .calorieStatus(calorieStatus)
                .caloriePercentage(caloriePercentage)
                .carb(carb)
                .fat(fat)
                .protein(protein)
                .build();
    }

    public HomeFullResponseDTO getHomeFullResponse(Long userId, Long goalPeriodId) {
        return HomeFullResponseDTO.builder()
                .main(getHomeData(userId,goalPeriodId))
                .exercise(getHomeExercise(userId,goalPeriodId))
                .meal(getHomeMealPlan(userId,goalPeriodId))
                .build();
    }
}
