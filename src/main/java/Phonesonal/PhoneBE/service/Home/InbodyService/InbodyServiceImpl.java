package Phonesonal.PhoneBE.service.Home.InbodyService;

import Phonesonal.PhoneBE.domain.Inbody;
import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.common.GoalPeriod;
import Phonesonal.PhoneBE.repository.GoalPeriodRepository;
import Phonesonal.PhoneBE.repository.InbodyImageRepository;
import Phonesonal.PhoneBE.repository.UserRepository;
import Phonesonal.PhoneBE.security.CustomUserDetails;
import Phonesonal.PhoneBE.service.AI.OpenAiVisionService;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InbodyServiceImpl {

    private final Gson gson = new Gson();
    private final GoalPeriodRepository goalPeriodRepository;
    private final UserRepository userRepository;
    private final OpenAiVisionService openAiVisionService;
    private final InbodyPhotoUploadService inbodyPhotoUploadService;
    private final InbodyImageRepository inbodyImageRepository;


    public Inbody extractInbodyData(CustomUserDetails userDetails, MultipartFile inbodyPicture) throws IOException {

        Long goalPeriodId = userDetails.getUser().getGoalPeriod().getId();
        Long userId = userDetails.getUser().getId();

        GoalPeriod goalPeriod = goalPeriodRepository.findById(goalPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표 기간입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String inbodyPictureUrl = inbodyPhotoUploadService.createInbodyPhotoUpload(userDetails,inbodyPicture);

        // 2. Vision API 호출 및 구현
        String responseJson = openAiVisionService.analyzeInbodyImage(inbodyPictureUrl);

        // 3. OpenAI API 응답에서 실제 GPT 응답 추출 (JSON 구조 파싱 필요)
        // responseJson은 OpenAI API 전체 응답 문자열임
        JsonObject root = JsonParser.parseString(responseJson).getAsJsonObject();
        String content = root.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .get("message").getAsJsonObject()
                .get("content").getAsString();

        // GPT가 반환한 JSON 문자열을 Inbody로 변환
        Inbody extractedInbody = gson.fromJson(content, Inbody.class);

        // 추가 데이터 세팅
        extractedInbody.setUser(user);
        extractedInbody.setGoalPeriod(goalPeriod);
        extractedInbody.setDate(LocalDate.now());
        extractedInbody.setImageUrl(inbodyPictureUrl);

        // 저장 및 반환
        return inbodyImageRepository.save(extractedInbody);

    }
}
