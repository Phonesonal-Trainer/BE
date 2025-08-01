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


    public void saveWeightRecord(CustomUserDetails userDetails, WeightRecordRequestDTO dto) {
        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getCurrentGoalPeriodId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new RuntimeException("GoalPeriod not found"));

        WeightRecord weightRecord = WeightRecord.builder()
                .weight(dto.getWeight())
                .recordDate(dto.getRecordDate())
                .user(user)
                .goalPeriod(goalPeriod)
                .build();

        weightRecordRepository.save(weightRecord);
    }

    public WeightRecordResponseDTO getLatestWeight(WeightRecordResponseDTO dto) {
        WeightRecord getWeightRecord = weightRecordRepository.findLatestByUserId(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("몸무게 기록이 없습니다."));

        return WeightRecordResponseDTO.builder()
                .userId(getWeightRecord.getUser().getId())
                .weight(getWeightRecord.getWeight())
                .recordDate(getWeightRecord.getRecordDate())
                .build();
    }
}
