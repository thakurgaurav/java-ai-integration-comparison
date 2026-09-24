package com.gauravthakur.ai.comparison.config;

import com.gauravthakur.ai.comparison.adapter.AiChatAdapter;
import com.gauravthakur.ai.comparison.adapter.springai.SpringAiChatAdapter;
import com.gauravthakur.ai.comparison.model.ProviderType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAiConfiguration {

    @Bean("springAiOpenAiChatClient")
    public ChatClient springAiOpenAiChatClient(
            OpenAiChatModel openAiChatModel
    ) {
        return ChatClient.builder(openAiChatModel).build();
    }

    @Bean("springAiOllamaChatClient")
    public ChatClient springAiOllamaChatClient(
            OllamaChatModel ollamaChatModel
    ) {
        return ChatClient.builder(ollamaChatModel).build();
    }

    @Bean
    public AiChatAdapter springAiOpenAiAdapter(
            @Qualifier("springAiOpenAiChatClient")
            ChatClient chatClient,
            @Value("${comparison.openai.model}") String model
    ) {
        return new SpringAiChatAdapter(
                chatClient,
                ProviderType.OPENAI,
                model,
                30
        );
    }

    @Bean
    public AiChatAdapter springAiOllamaAdapter(
            @Qualifier("springAiOllamaChatClient")
            ChatClient chatClient,
            @Value("${comparison.ollama.model}") String model
    ) {
        return new SpringAiChatAdapter(
                chatClient,
                ProviderType.OLLAMA,
                model,
                40
        );
    }
}