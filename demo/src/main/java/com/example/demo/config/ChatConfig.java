package com.example.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ChatConfig {
    @Value("${chatbot.pythonApiUrl}")
    private String pythonApiUrl;
}
