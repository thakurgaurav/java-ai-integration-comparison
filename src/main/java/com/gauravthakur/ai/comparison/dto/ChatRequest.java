package com.gauravthakur.ai.comparison.dto;

import com.gauravthakur.ai.comparison.model.IntegrationType;
import com.gauravthakur.ai.comparison.model.ProviderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChatRequest(

        @NotBlank(message = "Prompt must not be blank")
        @Size(max = 10_000, message = "Prompt must not exceed 10,000 characters")
        String prompt,

        @NotNull(message = "Integration must be provided")
        IntegrationType integration,

        @NotNull(message = "Provider must be provided")
        ProviderType provider

) {
}