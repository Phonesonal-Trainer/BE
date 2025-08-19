package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.apiPayload.code.status.ErrorStatus;
import Phonesonal.PhoneBE.apiPayload.exception.handler.CommonExceptionHandler;
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

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeWeightRecordCommandServiceImpl implements HomeWeightRecordCommandService{
    private final WeightRecordRepository weightRecordRepository;
    private final UserRepository userRepository;
    private final GoalPeriodRepository goalPeriodRepository;

    public WeightRecord saveWeightRecord(CustomUserDetails userDetails, WeightRecordRequestDTO dto) {
        Long userId = userDetails.getUser().getId();
        Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.INVALID_GOAL_PERIOD));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.USER_NOT_FOUND_FOR_FIND_EMAIL));


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

        // 1. DB에서 최신 기록 우선 조회
        Optional<WeightRecord> latestRecordOpt = weightRecordRepository.findLatestByUserId(userId);

        if (latestRecordOpt.isPresent()) {
            WeightRecord latest = latestRecordOpt.get();
            return WeightRecordResponseDTO.builder()
                    .weight(latest.getWeight())
                    .recordDate(latest.getRecordDate())
                    .build();
        }

        // 2. DB 기록이 없으면 userDetails에 저장된 weight 보여주기
        BigDecimal existingWeight = userDetails.getUser().getWeight();

        if (existingWeight != null) {
            return WeightRecordResponseDTO.builder()
                    .weight(existingWeight)
                    .recordDate(userDetails.getUser().getCreated_at())
                    .build();
        }

        // 3. 둘 다 없으면 예외 던지기 or 기본값 처리
        throw new RuntimeException("몸무게 기록이 없습니다. ");

        /*Long userId = userDetails.getUser().getId();

        BigDecimal existingWeight = userDetails.getUser().getWeight();

        if (existingWeight != null) {
            // 기존 몸무게가 존재하면, 그 값으로 WeightRecord 객체를 생성해 바로 반환 (DB 저장 X)
            return WeightRecordResponseDTO.builder()
                    .weight(existingWeight)
                    .recordDate(userDetails.getUser().getCreated_at())
                    .build();
        }

        WeightRecord getWeightRecord = weightRecordRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new RuntimeException("몸무게 기록이 없습니다."));


        return WeightRecordResponseDTO.builder()
                .weight(getWeightRecord.getWeight())
                .recordDate(getWeightRecord.getRecordDate())
                .build();*/
    }

}
