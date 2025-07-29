package Phonesonal.PhoneBE.service.User;

import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    /**
     * 진단 결과 저장 (기존 것이 있으면 업데이트)
     */
    public Diagnosis saveDiagnosis(Diagnosis diagnosis) {
        // 기존 진단이 있는지 확인
        Optional<Diagnosis> existingDiagnosis = diagnosisRepository.findByUserId(diagnosis.getUser().getId());

        if (existingDiagnosis.isPresent()) {
            // 기존 진단 업데이트
            Diagnosis existing = existingDiagnosis.get();
            existing.setTargetWeight(diagnosis.getTargetWeight());
            existing.setTargetBMI(diagnosis.getTargetBMI());
            existing.setTargetMuscleMass(diagnosis.getTargetMuscleMass());
            existing.setTargetBodyFatRate(diagnosis.getTargetBodyFatRate());
            existing.setRecommendedNutrition(diagnosis.getRecommendedNutrition());
            existing.setRecommendedCalories(diagnosis.getRecommendedCalories());
            existing.setWorkoutFrequency(diagnosis.getWorkoutFrequency());
            existing.setCardioDaysPerWeek(diagnosis.getCardioDaysPerWeek());
            existing.setCardioMinutesPerWeek(diagnosis.getCardioMinutesPerWeek());
            existing.setStrengthTrainingDays(diagnosis.getStrengthTrainingDays());
            existing.setStrengthTrainingTime(diagnosis.getStrengthTrainingTime());
            existing.setOverallRecommendation(diagnosis.getOverallRecommendation());
            return diagnosisRepository.save(existing);
        } else {
            // 새로운 진단 저장
            return diagnosisRepository.save(diagnosis);
        }
    }

    /**
     * 사용자별 진단 조회
     */
    @Transactional(readOnly = true)
    public Optional<Diagnosis> getDiagnosisByUserId(Long userId) {
        return diagnosisRepository.findByUserId(userId);
    }

    /**
     * 진단 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean hasDiagnosis(Long userId) {
        return diagnosisRepository.existsByUserId(userId);
    }
}