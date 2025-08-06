package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.WeeklyStamp;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.repository.WeeklyStampRepository;
import Phonesonal.PhoneBE.web.dto.WeeklyStampDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WeeklyStampServiceImpl implements WeeklyStampService {

    private final WeeklyStampRepository weeklyStampRepository;
    private final UserRepository userRepository;

    /**
     * 주간 스탬프 조회
     */
    @Transactional
    @Override
    public WeeklyStampDTO getWeeklyStamp(Long userId, LocalDate weekStartDate) {
        LocalDate mondayDate = weekStartDate.with(DayOfWeek.MONDAY);

        WeeklyStamp stamp = weeklyStampRepository
                .findByUserIdAndWeekStartDate(userId, mondayDate)
                .orElse(createEmptyStamp(userId, mondayDate));

        return WeeklyStampDTO.builder()
                .weekStartDate(stamp.getWeekStartDate())
                .mondayStamp(stamp.getMondayStamp())
                .tuesdayStamp(stamp.getTuesdayStamp())
                .wednesdayStamp(stamp.getWednesdayStamp())
                .thursdayStamp(stamp.getThursdayStamp())
                .fridayStamp(stamp.getFridayStamp())
                .saturdayStamp(stamp.getSaturdayStamp())
                .sundayStamp(stamp.getSundayStamp())
                .build();
    }

    /**
     * 새로운 주간 스탬프 생성
     */
    private WeeklyStamp createNewWeeklyStamp(Long userId, LocalDate weekStartDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        return WeeklyStamp.builder()
                .user(user)
                .weekStartDate(weekStartDate)
                .build();
    }

    /**
     * 빈 스탬프 객체 생성 (조회용)
     */
    private WeeklyStamp createEmptyStamp(Long userId, LocalDate weekStartDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        return WeeklyStamp.builder()
                .user(user)
                .weekStartDate(weekStartDate)
                .build();
    }
}
