package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.WeeklyStampService;
import Phonesonal.PhoneBE.web.dto.WeeklyStampDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stamps")
@Tag(name = "WeeklyStamp", description = "주간 운동 스탬프 관련 API")
public class WeeklyStampController {

    private final WeeklyStampService weeklyStampService;



    @Operation(
            summary = "주간 스탬프 조회",
            description = "특정 주의 스탬프 정보 조회. weekStart 파라미터가 없으면 이번 주 조회"
    )
    @GetMapping
    public ApiResponse<WeeklyStampDTO> getWeeklyStamp(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
    ) {
        Long userId = userDetails.getUser().getId();

        // weekStart가 없으면 이번 주로 설정
        LocalDate targetWeek = (weekStart != null) ? weekStart : LocalDate.now();

        WeeklyStampDTO stamp = weeklyStampService.getWeeklyStamp(userId, targetWeek);
        return ApiResponse.onSuccess(stamp);
    }
}
