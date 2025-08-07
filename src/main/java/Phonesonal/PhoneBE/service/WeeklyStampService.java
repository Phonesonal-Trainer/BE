package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.web.dto.WeeklyStampDTO;

import java.time.LocalDate;

public interface WeeklyStampService {

    //void completeTodayExercise(Long userId);// 오늘 운동 완료 처리
    WeeklyStampDTO getWeeklyStamp(Long userId, LocalDate weekStartDate); // 해당 주의 스탬프 정보 조회
}
