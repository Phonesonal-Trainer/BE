package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.WeightRecord;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.repository.WeightRecordRepository;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordRequestDTO;
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
}
