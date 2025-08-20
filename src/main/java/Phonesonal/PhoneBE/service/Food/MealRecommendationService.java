package Phonesonal.PhoneBE.service.Food;

public interface MealRecommendationService {
    // 최신 식단 피드백을 읽어 다음 주차 식단을 생성(복사/스케일/재생성)한다.
    void regenerateNextWeekByFeedback(Long userId);
}
