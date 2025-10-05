package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;


@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private final AppConfig appConfig;

    @Bean
    public OpenAPI customOpenAPI() {
        String activeUrl = appConfig.getActiveUrl();
        return new OpenAPI()
                .servers(List.of(
                        new Server().url(activeUrl)
                                .description(appConfig.isUseNgrok() ? "Ngrok Server" : "Local Server")
                ));
    }

}
