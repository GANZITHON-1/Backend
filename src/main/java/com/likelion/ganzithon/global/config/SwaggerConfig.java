package com.likelion.ganzithon.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "GANZITHON API",
                description = "간지톤 팀 살펴 API 명세서",
                version = "v1"
        ),
        servers = {
                @Server(url = "https://salpyeo.store", description = "Production Server"),
                @Server(url = "http://localhost:8080", description = "Local Server")
        }
)
public class SwaggerConfig {
}
