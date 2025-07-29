package Phonesonal.PhoneBE.apiPayload.code.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static int calculateWeek(LocalDate startDate, LocalDate now) {
        // 기준일의 주의 월요일
        LocalDate startMonday = startDate.with(DayOfWeek.MONDAY);
        // 현재 날짜의 주의 월요일
        LocalDate currentMonday = now.with(DayOfWeek.MONDAY);

        // 주차 차이 계산 (1주차부터 시작)
        long weeksBetween = ChronoUnit.WEEKS.between(startMonday, currentMonday);
        return (int) weeksBetween+1;
    }
}
