package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.WeightRecord;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.repository.WeightRecordRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordRequestDTO;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceWeightRecordImpl {
    private final WeightRecordRepository weightRecordRepository;
    private final UserRepository userRepository;
    private final GoalPeriodRepository goalPeriodRepository;


    public WeightRecord saveWeightRecord(CustomUserDetails userDetails, WeightRecordRequestDTO dto) {
        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WeightRecord weightRecord = WeightRecord.builder()
                .weight(dto.getWeight())
                .recordDate(dto.getRecordDate())
                .user(user)
                .goalPeriod(goalPeriod)
                .build();

        return weightRecordRepository.save(weightRecord);
    }

    public WeightRecordResponseDTO getLatestWeight(CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();

        WeightRecord getWeightRecord = weightRecordRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new RuntimeException("몸무게 기록이 없습니다."));

        return WeightRecordResponseDTO.builder()
                .userId(userId)
                .weight(getWeightRecord.getWeight())
                .recordDate(getWeightRecord.getRecordDate())
                .build();
    }

}
