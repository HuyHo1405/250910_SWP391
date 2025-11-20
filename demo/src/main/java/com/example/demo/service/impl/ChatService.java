package com.example.demo.service.impl;

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

    private final String PYTHON_API_URL = "http://localhost:5000/chat";

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
            Map response = restTemplate.postForObject(PYTHON_API_URL, entity, Map.class);

            // Lấy câu trả lời từ JSON trả về (key là "reply")
            if (response != null && response.containsKey("reply")) {
                return response.get("reply").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi: Không kết nối được với AI Server (Python).";
        }

        return "Lỗi không xác định.";
    }
}
