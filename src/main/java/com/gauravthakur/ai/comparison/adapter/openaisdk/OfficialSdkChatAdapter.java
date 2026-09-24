package com.gauravthakur.ai.comparison.adapter.openaisdk;

import com.gauravthakur.ai.comparison.adapter.AiChatAdapter;
import com.gauravthakur.ai.comparison.model.IntegrationType;
import com.gauravthakur.ai.comparison.model.ProviderType;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.core.Ordered;

public class OfficialSdkChatAdapter implements AiChatAdapter, Ordered {

    private final OpenAIClient client;
    private final ProviderType provider;
    private final String model;
    private final int order;

    public OfficialSdkChatAdapter(
            OpenAIClient client,
            ProviderType provider,
            String model,
            int order
    ) {
        this.client = client;
        this.provider = provider;
        this.model = model;
        this.order = order;
    }

    @Override
    public IntegrationType integration() {
        return IntegrationType.OPENAI_SDK;
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
        ChatCompletionCreateParams parameters =
                ChatCompletionCreateParams.builder()
                        .model(model)
                        .addUserMessage(prompt)
                        .build();

        ChatCompletion completion =
                client.chat().completions().create(parameters);

        return completion.choices().stream()
                .findFirst()
                .flatMap(choice -> choice.message().content())
                .filter(content -> !content.isBlank())
                .orElseThrow(() -> new IllegalStateException(
                        "The model returned no text content"
                ));
    }

    @Override
    public int getOrder() {
        return order;
    }
}