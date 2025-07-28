package Phonesonal.PhoneBE.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
@Getter
public class DiagnosisResultDTO {
    private BigDecimal weight;       // 기존 몸무게
    private BigDecimal targetWeight; // 목표 몸무게 (kg)
    private BigDecimal BMI;          // 기존 BMI
    private BigDecimal targetBMI;    // 목표 BMI
    private String targetMuscleMass; // 목표 골격근량 변동
    private BigDecimal bodyFatRate;  // 기존 체지방률
    private BigDecimal targetBodyFatRate; // 목표 체지방률 (%) - 선택사항
    private String recommendedNutrition; //권장 주 영양소 성향
    private int recommendedCalories;   //권장 일일 칼로리
    private int workoutFrequency;   //주간 운동 횟수
    private int cardioDaysPerWeek;  //주간 유산소 일수
    private int cardioMinutesPerWeek; //주간 유산소 시간
    private int strengthTrainingDays;   //주간 근력운동 일수
    private int strengthTrainingTime;   //주간 근력운동 시간
    private String overallRecommendation;   //진단 결과 한줄 요약
}