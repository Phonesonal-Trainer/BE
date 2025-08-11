package Phonesonal.PhoneBE.service.AI;

import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.exercise.Exercise;

import java.util.List;

public interface GeminiExerciseService {
    String generateWeeklyExerciseRecommendation(User user, Diagnosis diagnosis,
                                                List<Exercise> availableExercises,
                                                List<Long> excludeExerciseIds);
}
