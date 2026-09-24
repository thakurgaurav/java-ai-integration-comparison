package com.gauravthakur.ai.comparison.adapter;

import com.gauravthakur.ai.comparison.model.IntegrationType;
import com.gauravthakur.ai.comparison.model.ProviderType;

public interface AiChatAdapter {

    IntegrationType integration();

    ProviderType provider();

    String model();

    String chat(String prompt);

    default String resultKey() {
        return integration().value()
                + "-"
                + provider().value()
                + ":"
                + model();
    }
}