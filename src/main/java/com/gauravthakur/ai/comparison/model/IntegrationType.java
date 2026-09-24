package com.gauravthakur.ai.comparison.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum IntegrationType {

    OPENAI_SDK("openai-sdk"),
    SPRING_AI("spring-ai"),
    LANGCHAIN4J("langchain4j");

    private final String value;

    IntegrationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static IntegrationType fromValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported integration: " + value
                ));
    }
}