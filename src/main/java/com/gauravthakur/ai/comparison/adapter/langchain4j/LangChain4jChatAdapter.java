package com.gauravthakur.ai.comparison.adapter.langchain4j;

import com.gauravthakur.ai.comparison.adapter.AiChatAdapter;
import com.gauravthakur.ai.comparison.model.IntegrationType;
import com.gauravthakur.ai.comparison.model.ProviderType;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.core.Ordered;

public class LangChain4jChatAdapter implements AiChatAdapter, Ordered {

    private final ChatModel chatModel;
    private final ProviderType provider;
    private final String model;
    private final int order;

    public LangChain4jChatAdapter(
            ChatModel chatModel,
            ProviderType provider,
            String model,
            int order
    ) {
        this.chatModel = chatModel;
        this.provider = provider;
        this.model = model;
        this.order = order;
    }

    @Override
    public IntegrationType integration() {
        return IntegrationType.LANGCHAIN4J;
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
        String content = chatModel.chat(prompt);

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