package com.example.demo.controller;

import com.example.demo.model.dto.ChatRequest;
import com.example.demo.service.interfaces.IChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
@Tag(name = "Chat Bot", description = "Endpoints for interacting with the chat bot - Authorized User")
public class ChatController {
    private final IChatService chatService;

    @PostMapping("/ask")
    @Operation(
        summary = "Ask chat bot",
        description = "Allows a logged-in user to ask a question to the chat bot and receive an answer."
    )
    public ResponseEntity<String> askBot(
            @RequestBody @Valid ChatRequest chatRequest
    ) {
        return ResponseEntity.ok(chatService.getAnswerFromAI(chatRequest.getQuestion()));
    }
}
