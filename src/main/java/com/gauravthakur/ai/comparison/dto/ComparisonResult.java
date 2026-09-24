package com.gauravthakur.ai.comparison.dto;

import com.gauravthakur.ai.comparison.model.IntegrationType;
import com.gauravthakur.ai.comparison.model.ProviderType;

public record ComparisonResult(
        IntegrationType integration,
        ProviderType provider,
        String model,
        String message,
        long durationMs,
        ComparisonStatus status,
        String error
) {

    public static ComparisonResult success(
            IntegrationType integration,
            ProviderType provider,
            String model,
            String message,
            long durationMs
    ) {
        return new ComparisonResult(
                integration,
                provider,
                model,
                message,
                durationMs,
                ComparisonStatus.SUCCESS,
                null
        );
    }

    public static ComparisonResult failure(
            IntegrationType integration,
            ProviderType provider,
            String model,
            long durationMs,
            String error
    ) {
        return new ComparisonResult(
                integration,
                provider,
                model,
                null,
                durationMs,
                ComparisonStatus.FAILED,
                error
        );
    }
}