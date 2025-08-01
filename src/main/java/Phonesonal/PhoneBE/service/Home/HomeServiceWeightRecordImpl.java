package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.WeightRecord;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.repository.WeightRecordRepository;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordRequestDTO;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceWeightRecordImpl {
    private final WeightRecordRepository weightRecordRepository;
    private final UserRepository userRepository;


    public void saveWeightRecord(WeightRecordRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        WeightRecord weightRecord = WeightRecord.builder()
                .weight(dto.getWeight())
                .recordDate(dto.getRecordDate())
                .user(user)
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
