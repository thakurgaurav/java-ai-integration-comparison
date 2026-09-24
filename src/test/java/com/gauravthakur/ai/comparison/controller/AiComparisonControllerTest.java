package com.gauravthakur.ai.comparison.controller;

import com.gauravthakur.ai.comparison.dto.ChatComparisonRequest;
import com.gauravthakur.ai.comparison.dto.ChatRequest;
import com.gauravthakur.ai.comparison.dto.ComparisonResult;
import com.gauravthakur.ai.comparison.model.IntegrationType;
import com.gauravthakur.ai.comparison.model.ProviderType;
import com.gauravthakur.ai.comparison.service.ComparisonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiComparisonControllerTest {

    @Mock
    private ComparisonService comparisonService;

    @Test
    void compareReturnsOkStatus() {
        when(comparisonService.compare("hello")).thenReturn(Map.of());
        AiComparisonController controller = new AiComparisonController(comparisonService);

        ResponseEntity<Map<String, ComparisonResult>> response =
                controller.compare(new ChatComparisonRequest("hello"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void compareReturnsServiceResults() {
        ComparisonResult result = ComparisonResult.success(
            IntegrationType.OPENAI_SDK,
            ProviderType.OPENAI,
            "gpt-4o",
            "response",
            10
        );
        Map<String, ComparisonResult> expected = Map.of("openai", result);
        when(comparisonService.compare("hello")).thenReturn(expected);
        AiComparisonController controller = new AiComparisonController(comparisonService);

        ResponseEntity<Map<String, ComparisonResult>> response =
                controller.compare(new ChatComparisonRequest("hello"));

        assertSame(expected, response.getBody());
    }

    @Test
    void compareDelegatesPromptToService() {
        when(comparisonService.compare("compare these models")).thenReturn(Map.of());
        AiComparisonController controller = new AiComparisonController(comparisonService);

        controller.compare(new ChatComparisonRequest("compare these models"));

        verify(comparisonService).compare("compare these models");
    }

    @Test
    void compareSupportsEmptyServiceResults() {
        when(comparisonService.compare("hello")).thenReturn(Map.of());
        AiComparisonController controller = new AiComparisonController(comparisonService);

        ResponseEntity<Map<String, ComparisonResult>> response =
                controller.compare(new ChatComparisonRequest("hello"));

        assertEquals(Map.of(), response.getBody());
    }

    @Test
    void comparePropagatesServiceFailure() {
        RuntimeException failure = new RuntimeException("comparison failed");
        when(comparisonService.compare("hello")).thenThrow(failure);
        AiComparisonController controller = new AiComparisonController(comparisonService);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.compare(new ChatComparisonRequest("hello"))
        );

        assertSame(failure, thrown);
    }

    @Test
    void chatReturnsOkStatus() {
        ChatRequest request = new ChatRequest(
                "hello",
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI
        );
        when(comparisonService.chat(request)).thenReturn(null);
        AiComparisonController controller = new AiComparisonController(comparisonService);

        ResponseEntity<ComparisonResult> response = controller.chat(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void chatReturnsServiceResult() {
        ChatRequest request = new ChatRequest(
                "hello",
                IntegrationType.SPRING_AI,
                ProviderType.OLLAMA
        );
        ComparisonResult expected = ComparisonResult.success(
                IntegrationType.SPRING_AI,
                ProviderType.OLLAMA,
                "llama3",
                "response",
                10
        );
        when(comparisonService.chat(request)).thenReturn(expected);
        AiComparisonController controller = new AiComparisonController(comparisonService);

        ResponseEntity<ComparisonResult> response = controller.chat(request);

        assertSame(expected, response.getBody());
    }

    @Test
    void chatDelegatesEntireRequestToService() {
        ChatRequest request = new ChatRequest(
                "summarize this",
                IntegrationType.LANGCHAIN4J,
                ProviderType.OPENAI
        );
        when(comparisonService.chat(request)).thenReturn(null);
        AiComparisonController controller = new AiComparisonController(comparisonService);

        controller.chat(request);

        verify(comparisonService).chat(request);
    }

    @Test
    void chatSupportsOpenAiSdkWithOpenAiProvider() {
        ChatRequest request = new ChatRequest(
                "hello",
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI
        );
        when(comparisonService.chat(request)).thenReturn(null);
        AiComparisonController controller = new AiComparisonController(comparisonService);

        controller.chat(request);

        verify(comparisonService).chat(request);
    }

    @Test
    void chatSupportsSpringAiWithOllamaProvider() {
        ChatRequest request = new ChatRequest(
                "hello",
                IntegrationType.SPRING_AI,
                ProviderType.OLLAMA
        );
        when(comparisonService.chat(request)).thenReturn(null);
        AiComparisonController controller = new AiComparisonController(comparisonService);

        controller.chat(request);

        verify(comparisonService).chat(request);
    }

    @Test
    void chatPropagatesServiceFailure() {
        ChatRequest request = new ChatRequest(
                "hello",
                IntegrationType.OPENAI_SDK,
                ProviderType.OPENAI
        );
        RuntimeException failure = new RuntimeException("chat failed");
        when(comparisonService.chat(request)).thenThrow(failure);
        AiComparisonController controller = new AiComparisonController(comparisonService);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.chat(request)
        );

        assertSame(failure, thrown);
    }
}