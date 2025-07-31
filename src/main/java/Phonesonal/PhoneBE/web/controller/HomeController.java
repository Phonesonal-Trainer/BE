package Phonesonal.PhoneBE.web.controller;


import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.service.Home.HomeServiceImpl;
import Phonesonal.PhoneBE.web.dto.Home.HomeFullResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {
    private final HomeServiceImpl homeService; // 서비스 주입

    @GetMapping("/{userId}/main")
    public ApiResponse<HomeFullResponseDTO> homeFullResponse(@PathVariable Long userId) {
        HomeFullResponseDTO homeResponse = homeService.getHomeFullResponse(userId);
        return ApiResponse.onSuccess(homeResponse);
    }
/*
    @GetMapping("/{userId}/main/weight")
    public ApiResponse<HomeFullResponseDTO> homeFullResponse(@PathVariable Long userId) {
        HomeFullResponseDTO homeResponse = homeService.getHomeFullResponse(userId);
        return ApiResponse.onSuccess(homeResponse);
    }
    */
}
