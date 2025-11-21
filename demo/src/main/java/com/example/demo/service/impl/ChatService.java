package com.example.demo.service.impl;

import com.example.demo.config.ChatConfig;
import com.example.demo.exception.CommonException;
import com.example.demo.service.interfaces.IChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService implements IChatService {

    private final ChatConfig chatConfig;

    public String getAnswerFromAI(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        // Tạo dữ liệu gửi đi (JSON: {"message": "..."})
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", userMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Gửi POST request sang Python
            Map response = restTemplate.postForObject(chatConfig.getPythonApiUrl(), entity, Map.class);

            // Lấy câu trả lời từ JSON trả về (key là "reply")
            if (response != null && response.containsKey("reply")) {
                return response.get("reply").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new CommonException.ServiceUnavailable("Dịch vụ AI hiện không khả dụng. Vui lòng thử lại sau.");
    }
}
