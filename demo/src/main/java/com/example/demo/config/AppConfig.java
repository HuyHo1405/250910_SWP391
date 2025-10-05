package com.example.demo.config;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.ngrok-url}")
    private String ngrokUrl;

    @Value("${app.use-ngrok:false}")
    private boolean useNgrok;

    public String getActiveUrl() {
        return useNgrok ? ngrokUrl : baseUrl;
    }
}
