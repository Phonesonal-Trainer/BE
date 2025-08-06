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

//    @Operation(
//            summary = "오늘 운동 완료 처리",
//            description = "사용자가 오늘의 모든 운동을 완료했다고 선언할 때 호출. 모든 운동이 완료되어야 스탬프 획득"
//    )
//    @PostMapping("/complete-today")
//    public ApiResponse<String> completeTodayExercise(
//            @AuthenticationPrincipal CustomUserDetails userDetails
//    ) {
//        Long userId = userDetails.getUser().getId();
//        weeklyStampService.completeTodayExercise(userId);
//        return ApiResponse.onSuccess("오늘의 스탬프를 획득했습니다!");
//    }

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
