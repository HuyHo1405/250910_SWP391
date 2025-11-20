package com.example.demo.controller;

import com.example.demo.service.interfaces.IChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
@Tag(name = "Chat Bot")
public class ChatController {
    private final IChatService chatService;

    @PostMapping("/ask")
    public ResponseEntity<String> askBot(@RequestBody String question) {
        // Gọi service để lấy câu trả lời từ Python
        return ResponseEntity.ok(chatService.getAnswerFromAI(question));
    }
}
