package com.gauravthakur.ai.comparison.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatComparisonRequest(

        @NotBlank(message = "Prompt must not be blank")
        @Size(max = 10_000, message = "Prompt must not exceed 10,000 characters")
        String prompt

) {}