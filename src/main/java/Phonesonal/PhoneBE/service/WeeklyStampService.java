package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.web.dto.WeeklyStampDTO;

import java.time.LocalDate;

public interface WeeklyStampService {

    WeeklyStampDTO getWeeklyStamp(Long userId, LocalDate weekStartDate); // 해당 주의 스탬프 정보 조회
}
