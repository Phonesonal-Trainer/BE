package Phonesonal.PhoneBE.web.controller;


import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.BodyPhoto;
import Phonesonal.PhoneBE.domain.WeightRecord;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Home.BodyPhotoServiceImpl;
import Phonesonal.PhoneBE.service.Home.HomeServiceImpl;
import Phonesonal.PhoneBE.service.Home.HomeServiceWeightRecordImpl;
import Phonesonal.PhoneBE.web.dto.Home.BodyPhoto.BodyPhotoRequestDTO;
import Phonesonal.PhoneBE.web.dto.Home.BodyPhoto.BodyPhotoResponseDTO;
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
    private final BodyPhotoServiceImpl bodyPhotoService;

    @GetMapping("/{userId}/main")
    @Operation(summary = "홈화면 조회 API", description = "홈화면")
    public ApiResponse<HomeFullResponseDTO> homeFullResponse(@AuthenticationPrincipal CustomUserDetails userDetails) {
        HomeFullResponseDTO homeResponse = homeService.getHomeFullResponse(userDetails.getUser().getId(),userDetails.getUser().getCurrentGoalPeriodId());
        return ApiResponse.onSuccess(homeResponse);
    }

    @PostMapping("/{userId}/main/post-weight-record")
    @Operation(summary = "홈화면 몸무게 기록 API", description = "홈화면 몸무게 기록")
    public ApiResponse<String> saveWeight(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestBody WeightRecordRequestDTO request) {
        WeightRecord weightRecord = homeServiceWeightRecord.saveWeightRecord(userDetails, request);
        String answer = "UserId :" + weightRecord.getUser().getId() + ", SavedWeightRecord : " + weightRecord.getWeight();
        return ApiResponse.onSuccess(answer);
    }

    @GetMapping("/{userId}/main/get-weight-wecord")
    @Operation(summary = "홈화면 현재 몸무게 확인 API", description = "홈화면 몸무게 기록")
    public ApiResponse<WeightRecordResponseDTO> getWeight(@AuthenticationPrincipal CustomUserDetails userDetails) {

        WeightRecordResponseDTO getweightRecord = homeServiceWeightRecord.getLatestWeight(userDetails);

        return ApiResponse.onSuccess(getweightRecord);
    }

    @GetMapping("/{userId}/main/get-bodyphoto")
    @Operation(summary = "홈화면 현재 눈바디 확인 API", description = "홈화면 현재 눈바디")
    public ApiResponse<BodyPhotoResponseDTO> getLatestPhoto(@AuthenticationPrincipal CustomUserDetails userDetails) {
        BodyPhotoResponseDTO getBodyPhoto = bodyPhotoService.getBodyPhotoMetaData(userDetails);
        return ApiResponse.onSuccess(getBodyPhoto);
    }

    @PostMapping("/{userId}/main/post-bodyphoto")
    @Operation(summary = "홈화면 현재 눈바디 저장 API", description = "홈화면 눈바디 저장")
    public ApiResponse<String> postBodyPhoto(@AuthenticationPrincipal CustomUserDetails userDetails, BodyPhotoRequestDTO request) {
        BodyPhoto getBodyPhoto = bodyPhotoService.saveBodyPhotoMetadata(userDetails, request);
        String answer = "UserId : "+getBodyPhoto.getUser().getId() + ", Filename : " + getBodyPhoto.getFileName() + ", FilePath : " + getBodyPhoto.getFilePath();
        return ApiResponse.onSuccess(answer);
    }

}
