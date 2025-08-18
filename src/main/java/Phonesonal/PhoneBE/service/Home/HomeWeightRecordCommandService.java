package Phonesonal.PhoneBE.service.Home;

import Phonesonal.PhoneBE.domain.WeightRecord;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordRequestDTO;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordResponseDTO;

public interface HomeWeightRecordCommandService {
    WeightRecord saveWeightRecord(CustomUserDetails userDetails, WeightRecordRequestDTO dto);
    WeightRecordResponseDTO getLatestWeight(CustomUserDetails userDetails);
}
