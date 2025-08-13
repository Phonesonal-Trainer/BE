package Phonesonal.PhoneBE.service.AI;


import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OpenAiVisionService {

    private final OkHttpClient httpClient = new OkHttpClient();

    @Value("${openai.api-key}")
    private String apiKey;

    public String analyzeInbodyImage(String imageUrl) throws IOException {
        String jsonRequest = """
{
  "model": "gpt-4o",
  "messages": [
    {
      "role": "user",
      "content": [
        {
          "type": "text",
          "text": "이 인바디 결과 이미지에서 다음 항목의 값을 찾아 JSON으로 반환하세요:\\n{\\n  \\\"muscle_mass\\\": \\\"골격근량(kg)\\\",\\n  \\\"body_fat_mass\\\": \\\"체지방량(kg)\\\",\\n  \\\"weight\\\": \\\"체중(kg)\\\"\\n}"
        },
        {
          "type": "image_url",
          "image_url": {
            "url": "%s"
          }
        }
      ]
    }
  ],
  "max_tokens": 1000
}
""".formatted(imageUrl);



        RequestBody body = RequestBody.create( MediaType.get("application/json"),jsonRequest);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenAI API error: " + response.code() + " " + response.message());
            }
            return response.body().string();
        }
    }
}
