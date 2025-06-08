package com.pontual_telemetria.pontual_monitor_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Pontual Monitor API",
                version = "V1",
                description = "Documentaçao da API do sistema Pontual Monitor"
        )
)

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                final String securityScheme = "BearerAuth";

                return new OpenAPI()
                        .addSecurityItem(new SecurityRequirement().addList(securityScheme))
                        .components(new Components()
                                .addSecuritySchemes(securityScheme,
                                        new SecurityScheme()
                                                .name(securityScheme)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .in(SecurityScheme.In.HEADER)

                                )
                        );
        }
}
