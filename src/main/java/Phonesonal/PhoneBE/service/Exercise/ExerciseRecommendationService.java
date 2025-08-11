package Phonesonal.PhoneBE.service.Exercise;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.enums.ExerciseFeedback;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseRecommendationService {

    // 진단 완료 후 첫 주차 운동 추천 생성
    void generateInitialWeeklyRecommendation(Long userId);
    // 현재 주차 운동 재추천 (기존 운동 제외)
    void regenerateCurrentWeekRecommendation(Long userId);
    // 주간 운동 추천 생성 (재추천 포함)
    void generateWeeklyRecommendation(User user, List<Long> excludeExerciseIds);
    // 피드백 기반 처리
    void handleWeeklyFeedback(Long userId, ExerciseFeedback feedback, LocalDate weekStart);
    // 주간 피드백 배치 처리
    void processWeeklyFeedbackBatch();
}
