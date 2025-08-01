package Phonesonal.PhoneBE.web.controller;


import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.WeightRecord;
import Phonesonal.PhoneBE.repository.WeightRecordRepository;
import Phonesonal.PhoneBE.service.Home.HomeServiceImpl;
import Phonesonal.PhoneBE.service.Home.HomeServiceWeightRecordImpl;
import Phonesonal.PhoneBE.web.dto.Home.HomeFullResponseDTO;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordRequestDTO;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {
    private final HomeServiceImpl homeService; // 서비스 주입
    private final HomeServiceWeightRecordImpl homeServiceWeightRecord;
    private final WeightRecordRepository weightRecordRepository;

    @GetMapping("/{userId}/main")
    @Operation(summary = "홈화면 조회 API", description = "홈화면")
    public ApiResponse<HomeFullResponseDTO> homeFullResponse(@PathVariable Long userId) {
        HomeFullResponseDTO homeResponse = homeService.getHomeFullResponse(userId);
        return ApiResponse.onSuccess(homeResponse);
    }

    @PostMapping("/{userId}/main/post-weight-record")
    @Operation(summary = "홈화면 몸무게 기록 API", description = "홈화면 몸무게 기록")
    public ApiResponse<Long> saveWeight(@RequestBody WeightRecordRequestDTO dto) {
        homeServiceWeightRecord.saveWeightRecord(dto);
        return ApiResponse.onSuccess(dto.getUserId());
    }

    @GetMapping("/{userId}/main/get-weight-wecord")
    @Operation(summary = "홈화면 현재 몸무게 확인 API", description = "홈화면 몸무게 기록")
    public ApiResponse<WeightRecordResponseDTO> getWeight(@PathVariable Long userId) {

        WeightRecord weightRecord = weightRecordRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new RuntimeException("몸무게 기록이 없습니다."));

        WeightRecordResponseDTO getweightRecord = WeightRecordResponseDTO.builder()
                .userId(weightRecord.getUser().getId())
                .weight(weightRecord.getWeight())
                .recordDate(weightRecord.getRecordDate())
                .build();
        return ApiResponse.onSuccess(getweightRecord);
    }

}
