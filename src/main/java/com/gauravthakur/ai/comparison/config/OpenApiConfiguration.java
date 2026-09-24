package com.gauravthakur.ai.comparison.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI javaAiComparisonOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Java AI Integration Comparison API")
                        .version("1.0.0")
                        .description("""
                                Compare the official OpenAI Java SDK,
                                Spring AI and LangChain4j using OpenAI
                                and local Ollama models.
                                """));
    }
}