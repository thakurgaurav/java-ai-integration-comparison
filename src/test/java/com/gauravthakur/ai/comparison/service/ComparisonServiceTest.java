package com.gauravthakur.ai.comparison.service;

import com.gauravthakur.ai.comparison.adapter.AiChatAdapter;
import com.gauravthakur.ai.comparison.dto.ChatRequest;
import com.gauravthakur.ai.comparison.dto.ComparisonResult;
import com.gauravthakur.ai.comparison.dto.ComparisonStatus;
import com.gauravthakur.ai.comparison.model.IntegrationType;
import com.gauravthakur.ai.comparison.model.ProviderType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComparisonServiceTest {

    @Test
    void compareReturnsEmptyMapWhenNoAdaptersAreConfigured() {
        ComparisonService service = new ComparisonService(List.of(), Runnable::run);

        Map<String, ComparisonResult> results = service.compare("hello");

        assertTrue(results.isEmpty());
    }

    @Test
    void compareReturnsSuccessForEachAdapterInAdapterOrder() {
        AiChatAdapter openAiAdapter = adapter(
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI,
                "gpt-4o",
                "openai-result"
        );
        AiChatAdapter ollamaAdapter = adapter(
                IntegrationType.SPRING_AI,
                ProviderType.OLLAMA,
                "llama3",
                "ollama-result"
        );
        when(openAiAdapter.chat("hello")).thenReturn("OpenAI response");
        when(ollamaAdapter.chat("hello")).thenReturn("Ollama response");
        ComparisonService service = new ComparisonService(
                List.of(openAiAdapter, ollamaAdapter),
                Runnable::run
        );

        Map<String, ComparisonResult> results = service.compare("hello");

        assertEquals(List.of("openai-result", "ollama-result"), results.keySet().stream().toList());
        assertEquals("OpenAI response", results.get("openai-result").message());
        assertEquals("Ollama response", results.get("ollama-result").message());
        assertEquals(ComparisonStatus.SUCCESS, results.get("openai-result").status());
        assertEquals(ComparisonStatus.SUCCESS, results.get("ollama-result").status());
    }

    @Test
    void compareSendsTheSamePromptToEveryAdapter() {
        AiChatAdapter firstAdapter = adapter(
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI,
                "gpt-4o",
                "first"
        );
        AiChatAdapter secondAdapter = adapter(
                IntegrationType.LANGCHAIN4J,
                ProviderType.OPENAI,
                "gpt-4o",
                "second"
        );
        ComparisonService service = new ComparisonService(
                List.of(firstAdapter, secondAdapter),
                Runnable::run
        );

        service.compare("shared prompt");

        verify(firstAdapter).chat("shared prompt");
        verify(secondAdapter).chat("shared prompt");
    }

    @Test
    void compareConvertsAdapterExceptionToFailureResult() {
        AiChatAdapter failingAdapter = adapter(
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI,
                "gpt-4o",
                "openai-result"
        );
        when(failingAdapter.chat("hello")).thenThrow(new IllegalStateException("provider unavailable"));
        ComparisonService service = new ComparisonService(List.of(failingAdapter), Runnable::run);

        ComparisonResult result = service.compare("hello").get("openai-result");

        assertEquals(ComparisonStatus.FAILED, result.status());
        assertEquals("provider unavailable", result.error());
        assertTrue(result.durationMs() >= 0);
    }

    @Test
    void chatRoutesToAdapterMatchingIntegrationAndProvider() {
        AiChatAdapter nonMatchingAdapter = adapter(
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI,
                "gpt-4o",
                "openai-result"
        );
        AiChatAdapter matchingAdapter = adapter(
                IntegrationType.SPRING_AI,
                ProviderType.OLLAMA,
                "llama3",
                "ollama-result"
        );
        when(matchingAdapter.chat("hello")).thenReturn("matched response");
        ComparisonService service = new ComparisonService(
                List.of(nonMatchingAdapter, matchingAdapter),
                Runnable::run
        );

        ComparisonResult result = service.chat(new ChatRequest(
                "hello",
                IntegrationType.SPRING_AI,
                ProviderType.OLLAMA
        ));

        assertEquals("matched response", result.message());
        assertEquals("llama3", result.model());
        verify(nonMatchingAdapter, never()).chat("hello");
        verify(matchingAdapter).chat("hello");
    }

    @Test
    void chatUsesTheFirstMatchingAdapter() {
        AiChatAdapter firstAdapter = adapter(
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI,
                "first-model",
                "first-result"
        );
        AiChatAdapter secondAdapter = adapter(
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI,
                "second-model",
                "second-result"
        );
        when(firstAdapter.chat("hello")).thenReturn("first response");
        ComparisonService service = new ComparisonService(
                List.of(firstAdapter, secondAdapter),
                Runnable::run
        );

        ComparisonResult result = service.chat(new ChatRequest(
                "hello",
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI
        ));

        assertEquals("first response", result.message());
        assertEquals("first-model", result.model());
        verify(secondAdapter, never()).chat("hello");
    }

    @Test
    void chatThrowsForUnsupportedRoute() {
        AiChatAdapter adapter = adapter(
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI,
                "gpt-4o",
                "openai-result"
        );
        ComparisonService service = new ComparisonService(List.of(adapter), Runnable::run);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.chat(new ChatRequest(
                        "hello",
                        IntegrationType.LANGCHAIN4J,
                        ProviderType.OLLAMA
                ))
        );

        assertEquals("Unsupported AI route: langchain4j/ollama", exception.getMessage());
    }

    @Test
    void chatConvertsAdapterExceptionToFailureResult() {
        AiChatAdapter failingAdapter = adapter(
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI,
                "gpt-4o",
                "openai-result"
        );
        doThrow(new RuntimeException("chat failed")).when(failingAdapter).chat("hello");
        ComparisonService service = new ComparisonService(List.of(failingAdapter), Runnable::run);

        ComparisonResult result = service.chat(new ChatRequest(
                "hello",
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI
        ));

        assertEquals(ComparisonStatus.FAILED, result.status());
        assertEquals("chat failed", result.error());
    }

    private static AiChatAdapter adapter(
            IntegrationType integration,
            ProviderType provider,
            String model,
            String resultKey
    ) {
        AiChatAdapter adapter = mock(AiChatAdapter.class);
        when(adapter.integration()).thenReturn(integration);
        when(adapter.provider()).thenReturn(provider);
        when(adapter.model()).thenReturn(model);
        when(adapter.resultKey()).thenReturn(resultKey);
        return adapter;
    }
}