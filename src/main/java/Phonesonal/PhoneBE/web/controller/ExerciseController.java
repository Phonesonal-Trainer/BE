package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
@Tag(name = "exercise", description = "운동 관련 API")
public class ExerciseController {

    private ExerciseService exerciseService;

    @Operation(summary = "운동 목록 조회")
    @GetMapping
    public ApiResponse<List<ExerciseResponseDto>> getMyExercises(
            //@AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        //Long userId = userDetails.getUser().getId();
        return ApiResponse.onSuccess(exerciseService.getMyExercises());
    }

}
