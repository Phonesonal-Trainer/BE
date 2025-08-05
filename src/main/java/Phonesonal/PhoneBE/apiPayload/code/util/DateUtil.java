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

    public static LocalDate[] getWeekDateRange(LocalDate startDate, LocalDate endDate, int week) {
        // 기준일의 주의 월요일 구하기
        LocalDate startMonday = startDate.with(DayOfWeek.MONDAY);

        // 입력된 주차에 해당하는 주의 월요일 구하기
        LocalDate targetMonday = startMonday.plusWeeks(week - 1);
        LocalDate targetSunday = targetMonday.plusDays(6);

        // endDate를 넘지 않도록 조정
        if (targetMonday.isAfter(endDate)) {
            return null; // 유효하지 않은 주차
        }
        if (targetSunday.isAfter(endDate)) {
            targetSunday = endDate; // 범위를 endDate까지 제한
        }

        return new LocalDate[]{targetMonday, targetSunday};
    }
}
