package Phonesonal.PhoneBE.service.Food;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.handler.CommonExceptionHandler;
import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.RecommendMeal;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.Feedback;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.domain.enums.FoodFeedback; // FEW, MANY, DISLIKE, SATISFIED
import Phonesonal.PhoneBE.repository.FeedbackRepository;
import Phonesonal.PhoneBE.repository.RecommendMealRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.service.AI.GeminiMealService;
import Phonesonal.PhoneBE.web.dto.Food.GenerateMealRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Phonesonal.PhoneBE.apiPayload.code.util.DateUtil;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealRecommendationServiceImpl implements MealRecommendationService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final RecommendMealRepository recommendMealRepository;
    private final GeminiMealService geminiMealService;

    @Scheduled(cron = "0 0 1 * * MON", zone = "Asia/Seoul")
    @Transactional
    public void regenerateByLastWeekFeedback() {
        LocalDate lastSunday = LocalDate.now().minusDays(1); // 월요일 새벽 기준: 어제=지난주 일요일

        userRepository.findAll().forEach(user -> {
            try {
                if (user.getGoalPeriod() == null) return;

                int lastWeek = DateUtil.calculateWeek(user.getGoalPeriod().getStartDate(), lastSunday);

                boolean hasFeedback = feedbackRepository.existsByUserIdAndGoalPeriod_IdAndWeek(
                        user.getId(), user.getGoalPeriod().getId(), lastWeek
                );
                if (!hasFeedback) return;

                // 여기서 같은 클래스의 다른 메서드를 호출해도,
                // 이미 이 @Scheduled 메서드에 @Transactional이 걸려 있으므로 트랜잭션 범위 안에서 실행됨.
                regenerateNextWeekByFeedback(user.getId());

            } catch (Exception e) {
                log.error("Meal weekly regeneration failed. userId={}", user.getId(), e);
            }
        });
    }

    @Transactional
    @Override
    public void regenerateNextWeekByFeedback(Long userId) {
        // 1) 유저/목표기간/진단 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        GoalPeriod gp = user.getGoalPeriod();
        if (gp == null) throw new CommonExceptionHandler(ErrorStatus.INVALID_GOAL_PERIOD);

        Diagnosis diagnosis = user.getDiagnosis();
        if (diagnosis == null) throw new CommonExceptionHandler(ErrorStatus.DIAGNOSIS_NOT_FOUND);

        // 2) 가장 최근 피드백 주차 기준
        Feedback latest = feedbackRepository
                .findTopByUserIdAndGoalPeriod_IdOrderByWeekDesc(userId, gp.getId())
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.FEEDBACK_NOT_FOUND));

        int srcWeek = latest.getWeek();
        LocalDate srcMonday = mondayOfWeek(gp.getStartDate(), srcWeek);
        LocalDate dstMonday = srcMonday.plusWeeks(1);
        LocalDate dstSunday = dstMonday.plusDays(6);

        // 3) 다음 주차 기존 추천 삭제(중복 방지) - goalPeriodId 포함해 안전하게 삭제
        recommendMealRepository.deleteByUserIdAndGoalPeriodIdAndDateBetween(
                userId, gp.getId(), dstMonday, dstSunday
        );

        // 4) 피드백 분기
        FoodFeedback fb = latest.getFoodFeedback();
        if (fb == null) throw new CommonExceptionHandler(ErrorStatus.FEEDBACK_NOT_FOUND);

        switch (fb) {
            case SATISFIED -> {
                // 만족 → 그대로 복사
                copyWeekWithScale(user, gp, srcMonday, dstMonday, 1.0);
            }
            case FEW -> {
                // "섭취량이 너무 적다" → 10% 줄이기 (요청 명세대로 0.9배)
                copyWeekWithScale(user, gp, srcMonday, dstMonday, 1.1);
            }
            case MANY -> {
                // "섭취량이 너무 많다" → 10% 늘리기
                copyWeekWithScale(user, gp, srcMonday, dstMonday, 0.9);
            }
            case DISLIKE -> {
                // "음식이 마음에 들지 않음" → Gemini로 재생성
                GenerateMealRequestDTO req = GenerateMealRequestDTO.builder()
                        .startDate(dstMonday)
                        .build();
                geminiMealService.generateAndSaveWeeklyAllMeals(user, diagnosis, req);
            }
            default -> {
                // 안전장치
                copyWeekWithScale(user, gp, srcMonday, dstMonday, 1.0);
            }
        }
    }

    private LocalDate mondayOfWeek(LocalDate goalStartDate, int week) {
        // 목표기간 시작일 기준 1주차 월요일부터 (week-1)주 가산
        LocalDate startMonday = goalStartDate.with(DayOfWeek.MONDAY);
        return startMonday.plusWeeks(Math.max(0, week - 1));
    }

    private void copyWeekWithScale(User user, GoalPeriod gp, LocalDate srcMonday, LocalDate dstMonday, double scale) {
        LocalDate srcSunday = srcMonday.plusDays(6);

        List<RecommendMeal> src = recommendMealRepository
                .findByUserIdAndGoalPeriodIdAndDateBetween(user.getId(), gp.getId(), srcMonday, srcSunday);

        if (src.isEmpty()) {
            // 원본 주차 식단이 없으면 재생성으로 대체
            log.warn("원본 주차({}~{}) 식단 없음 → 재생성 userId={}", srcMonday, srcSunday, user.getId());
            GenerateMealRequestDTO req = GenerateMealRequestDTO.builder()
                    .startDate(dstMonday)
                    .build();
            geminiMealService.generateAndSaveWeeklyAllMeals(user, user.getDiagnosis(), req);
            return;
        }

        for (RecommendMeal rm : src) {
            int dayOffset = rm.getDate().getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
            LocalDate newDate = dstMonday.plusDays(dayOffset);

            Float qty = rm.getQuantity(); // grams 컬럼
            Float scaled = (qty == null) ? null : clampGrams(Math.round(qty * (float) scale));

            RecommendMeal newRm = RecommendMeal.builder()
                    .date(newDate)
                    .mealTime(rm.getMealTime())
                    .quantity(scaled)
                    .food(rm.getFood())
                    .goalPeriod(gp)       // 명시적으로 현재 GoalPeriod 지정
                    .user(user)
                    .build();

            recommendMealRepository.save(newRm);
        }
    }

    // 최소 1g 보장. 0 허용 정책이면 Math.max(0, grams)로 변경
    private Float clampGrams(long grams) {
        long clamped = Math.max(1, grams);
        return (float) clamped;
    }
}
