package com.gauravthakur.ai.comparison.config;

import com.gauravthakur.ai.comparison.adapter.AiChatAdapter;
import com.gauravthakur.ai.comparison.adapter.langchain4j.LangChain4jChatAdapter;
import com.gauravthakur.ai.comparison.model.ProviderType;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangChain4jConfiguration {

    @Bean("langChain4jOpenAiChatModel")
    public ChatModel langChain4jOpenAiChatModel(
            @Value("${comparison.openai.api-key}") String apiKey,
            @Value("${comparison.openai.base-url}") String baseUrl,
            @Value("${comparison.openai.model}") String model
    ) {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(model)
                .timeout(Duration.ofMinutes(2))
                .build();
    }

    @Bean("langChain4jOllamaChatModel")
    public ChatModel langChain4jOllamaChatModel(
            @Value("${comparison.ollama.base-url}") String baseUrl,
            @Value("${comparison.ollama.model}") String model
    ) {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(model)
                .timeout(Duration.ofMinutes(5))
                .build();
    }

    @Bean
    public AiChatAdapter langChain4jOpenAiAdapter(
            @Qualifier("langChain4jOpenAiChatModel")
            ChatModel chatModel,
            @Value("${comparison.openai.model}") String model
    ) {
        return new LangChain4jChatAdapter(
                chatModel,
                ProviderType.OPENAI,
                model,
                50
        );
    }

    @Bean
    public AiChatAdapter langChain4jOllamaAdapter(
            @Qualifier("langChain4jOllamaChatModel")
            ChatModel chatModel,
            @Value("${comparison.ollama.model}") String model
    ) {
        return new LangChain4jChatAdapter(
                chatModel,
                ProviderType.OLLAMA,
                model,
                60
        );
    }
}