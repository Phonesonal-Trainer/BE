package Phonesonal.PhoneBE.web.controller;

import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.apiPayload.code.status.SuccessStatus;
import Phonesonal.PhoneBE.service.Food.FoodQueryService;
import Phonesonal.PhoneBE.web.dto.Food.SearchFoodResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Food", description = "음식 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/foods")
public class FoodController {

    private final FoodQueryService foodQueryService;

    @Operation(summary = "기존 음식 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchFoodResponseDTO>>> searchFoods(
            @RequestParam String keyword
    ) {
        List<SearchFoodResponseDTO> result = foodQueryService.searchFoods(keyword);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, result));
    }
}
