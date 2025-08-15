package Phonesonal.PhoneBE.web.controller;


import Phonesonal.PhoneBE.apiPayload.ApiResponse;
import Phonesonal.PhoneBE.domain.Inbody;
import Phonesonal.PhoneBE.domain.MealImage;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.Home.InbodyService.InbodyServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/inbody")
@RequiredArgsConstructor
public class InbodyController {

    private final InbodyServiceImpl inbodyService;
/*

    @PostMapping("/inbody_extract")
    public ResponseEntity<Inbody> extractInbody(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("file") MultipartFile file) throws Exception {
        Inbody result = inbodyService.extractInbodyData(userDetails, file);
        return ResponseEntity.ok(result);
    }
*/

    @PostMapping(value = "/inbody-images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<String> extractInbody(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestParam("file") MultipartFile file) { //여기서 @RequestPart는 프론트앤드에서 필요한값 프론트와 연동시 맞춰줘야함
        try {
            Inbody result = inbodyService.extractInbodyData(userDetails, file);
            return ApiResponse.onSuccess(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.onFailure(HttpStatus.INTERNAL_SERVER_ERROR.toString(),"Failed to process inbody image: ",e.getMessage());
        }

    }
}