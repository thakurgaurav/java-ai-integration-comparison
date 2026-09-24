package com.gauravthakur.ai.comparison.config;

import com.gauravthakur.ai.comparison.adapter.AiChatAdapter;
import com.gauravthakur.ai.comparison.adapter.openaisdk.OfficialSdkChatAdapter;
import com.gauravthakur.ai.comparison.model.ProviderType;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfficialSdkConfiguration {

    @Bean("officialOpenAiClient")
    public OpenAIClient officialOpenAiClient(
            @Value("${comparison.openai.api-key}") String apiKey,
            @Value("${comparison.openai.base-url}") String baseUrl
    ) {
        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
    }

    @Bean("officialOllamaClient")
    public OpenAIClient officialOllamaClient(
            @Qualifier("officialOpenAiClient") OpenAIClient openAiClient,
            @Value("${comparison.ollama.api-key}") String apiKey,
            @Value("${comparison.ollama.openai-compatible-base-url}")
            String baseUrl
    ) {
        return openAiClient.withOptions(options -> options
                .apiKey(apiKey)
                .baseUrl(baseUrl)
        );
    }

    @Bean
    public AiChatAdapter officialSdkOpenAiAdapter(
            @Qualifier("officialOpenAiClient") OpenAIClient client,
            @Value("${comparison.openai.model}") String model
    ) {
        return new OfficialSdkChatAdapter(
                client,
                ProviderType.OPENAI,
                model,
                10
        );
    }

    @Bean
    public AiChatAdapter officialSdkOllamaAdapter(
            @Qualifier("officialOllamaClient") OpenAIClient client,
            @Value("${comparison.ollama.model}") String model
    ) {
        return new OfficialSdkChatAdapter(
                client,
                ProviderType.OLLAMA,
                model,
                20
        );
    }

}