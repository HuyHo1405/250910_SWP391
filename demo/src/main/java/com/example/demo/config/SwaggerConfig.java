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
                        // Ngrok
                        new Server().url("https://41778679112c.ngrok-free.app").description("Ngrok server"),
                        // Local
                        new Server().url("http://localhost:8080/").description("Local server")
                ));
    }

}
