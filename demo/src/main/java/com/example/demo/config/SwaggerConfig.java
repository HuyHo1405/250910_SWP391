package com.example.demo.config;

import io.swagger.v3.oas.models.Components; // Import mới
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement; // Import mới
import io.swagger.v3.oas.models.security.SecurityScheme; // Import mới
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
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
        final String securitySchemeName = "bearerAuth"; // Tên của cơ chế bảo mật

        return new OpenAPI()
                // 1. Định nghĩa Security Scheme cho JWT (Bearer Token)
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP) // Loại HTTP
                                .scheme("bearer") // Scheme là "bearer"
                                .bearerFormat("JWT") // Format là JWT
                ))
                // 2. Yêu cầu Security Scheme này được áp dụng
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // 3. Giữ nguyên phần cấu hình Server của bạn
                .servers(List.of(
                        new Server().url(activeUrl)
                                .description(appConfig.isUseNgrok() ? "Ngrok Server" : "Local Server")
                ));
    }
}