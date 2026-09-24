package com.gauravthakur.ai.comparison.adapter.springai;

import com.gauravthakur.ai.comparison.adapter.AiChatAdapter;
import com.gauravthakur.ai.comparison.model.IntegrationType;
import com.gauravthakur.ai.comparison.model.ProviderType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.Ordered;

public class SpringAiChatAdapter implements AiChatAdapter, Ordered {

    private final ChatClient chatClient;
    private final ProviderType provider;
    private final String model;
    private final int order;

    public SpringAiChatAdapter(
            ChatClient chatClient,
            ProviderType provider,
            String model,
            int order
    ) {
        this.chatClient = chatClient;
        this.provider = provider;
        this.model = model;
        this.order = order;
    }

    @Override
    public IntegrationType integration() {
        return IntegrationType.SPRING_AI;
    }

    @Override
    public ProviderType provider() {
        return provider;
    }

    @Override
    public String model() {
        return model;
    }

    @Override
    public String chat(String prompt) {
        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        if (content == null || content.isBlank()) {
            throw new IllegalStateException(
                    "The model returned no text content"
            );
        }

        return content;
    }

    @Override
    public int getOrder() {
        return order;
    }
}