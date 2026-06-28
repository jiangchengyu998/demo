package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

    @Bean
    OpenAPI cloudDeployDemoOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cloud Deploy Demo API")
                        .version("v1")
                        .description("A small Spring Boot CRUD API for cloud one-click deployment."));
    }
}
