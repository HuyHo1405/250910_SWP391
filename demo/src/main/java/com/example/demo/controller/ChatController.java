package com.example.demo.controller;

import com.example.demo.service.interfaces.IChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Operation(
        summary = "[PRIVATE] [USER] Ask chat bot",
        description = "Allows a logged-in user to ask a question to the chat bot and receive an answer."
    )
    public ResponseEntity<String> askBot(
            @RequestBody
            @NotBlank(message = "nội dung câu hỏi không được để trống")
            String question
    ) {
        // Gọi service để lấy câu trả lời từ Python
        return ResponseEntity.ok(chatService.getAnswerFromAI(question));
    }
}
