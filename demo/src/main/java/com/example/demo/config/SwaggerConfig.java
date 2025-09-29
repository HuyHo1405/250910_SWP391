package com.example.demo.config;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        // Local
                        new Server().url("http://localhost:8080/").description("Local server"),
                        // Ngrok
                        new Server().url("https://4e0a5e993019.ngrok-free.app/").description("Ngrok server")
                ));
    }

}
