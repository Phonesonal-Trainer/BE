package Phonesonal.PhoneBE.service;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.WeeklyStamp;
import Phonesonal.PhoneBE.domain.enums.exercise.State;
import Phonesonal.PhoneBE.domain.mapping.UserExercise;
import Phonesonal.PhoneBE.repository.UserExerciseRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.repository.WeeklyStampRepository;
import Phonesonal.PhoneBE.web.dto.WeeklyStampDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyStampServiceImpl implements WeeklyStampService {

    private final WeeklyStampRepository weeklyStampRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final UserRepository userRepository;

//    /**
//     * 오늘 운동 완료 처리 - 사용자가 버튼 클릭 시 호출
//     */
//    @Transactional
//    @Override
//    public void completeTodayExercise(Long userId) {
//        LocalDate today = LocalDate.now();
//
//        // 1. 오늘의 모든 운동 조회
//        List<UserExercise> todayExercises = userExerciseRepository.findByUserIdAndExerciseDate(userId, today);
//
//        if (todayExercises.isEmpty()) {
//            throw new RuntimeException("오늘 운동 기록이 없습니다.");
//        }
//
//        // 2. 모든 운동이 완료되었는지 확인
//        boolean allCompleted = todayExercises.stream()
//                .allMatch(exercise -> exercise.getState() == State.completed);
//
//        if (!allCompleted) {
//            throw new RuntimeException("아직 완료되지 않은 운동이 있습니다.");
//        }
//
//        // 3. 주간 스탬프 조회 또는 생성
//        LocalDate weekStartDate = today.with(DayOfWeek.MONDAY);
//        WeeklyStamp weeklyStamp = weeklyStampRepository
//                .findByUserIdAndWeekStartDate(userId, weekStartDate)
//                .orElseGet(() -> createNewWeeklyStamp(userId, weekStartDate));
//
//        // 4. 오늘 요일의 스탬프 획득 처리
//        weeklyStamp.setDayStamp(today.getDayOfWeek(), true);
//        weeklyStampRepository.save(weeklyStamp);
//    }

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
