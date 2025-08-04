package Phonesonal.PhoneBE.web.controller;


import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.WeightRecord;
import Phonesonal.PhoneBE.repository.WeightRecordRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Home.HomeServiceImpl;
import Phonesonal.PhoneBE.service.Home.HomeServiceWeightRecordImpl;
import Phonesonal.PhoneBE.web.dto.Home.HomeFullResponseDTO;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordRequestDTO;
import Phonesonal.PhoneBE.web.dto.Home.WeightRecordResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ApiResponse<String> saveWeight(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestBody WeightRecordRequestDTO dto) {
        homeServiceWeightRecord.saveWeightRecord(userDetails, dto);
        return ApiResponse.onSuccess("몸무게 기록 완료");
    }

    @GetMapping("/{userId}/main/get-weight-wecord")
    @Operation(summary = "홈화면 현재 몸무게 확인 API", description = "홈화면 몸무게 기록")
    public ApiResponse<WeightRecordResponseDTO> getWeight(@AuthenticationPrincipal CustomUserDetails userDetails) {

        WeightRecordResponseDTO getweightRecord = homeServiceWeightRecord.getLatestWeight(userDetails);

        return ApiResponse.onSuccess(getweightRecord);
    }

}
