package Phonesonal.PhoneBE.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WeeklyStampDTO {
    private LocalDate weekStartDate; // 해당 주의 월요일

    // 각 요일별 스탬프 획득 상태
    private Boolean mondayStamp;
    private Boolean tuesdayStamp;
    private Boolean wednesdayStamp;
    private Boolean thursdayStamp;
    private Boolean fridayStamp;
    private Boolean saturdayStamp;
    private Boolean sundayStamp;
}
