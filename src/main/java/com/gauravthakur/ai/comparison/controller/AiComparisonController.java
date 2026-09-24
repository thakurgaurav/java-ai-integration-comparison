package com.gauravthakur.ai.comparison.controller;

import com.gauravthakur.ai.comparison.dto.ChatComparisonRequest;
import com.gauravthakur.ai.comparison.dto.ChatRequest;
import com.gauravthakur.ai.comparison.dto.ComparisonResult;
import com.gauravthakur.ai.comparison.service.ComparisonService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
public class AiComparisonController {

    private final ComparisonService comparisonService;

    public AiComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @PostMapping("/compare")
    public ResponseEntity<Map<String, ComparisonResult>> compare(
            @Valid @RequestBody ChatComparisonRequest request
    ) {
        log.info("Received compare request with prompt: {}", request.prompt());
        Map<String, ComparisonResult> result = comparisonService.compare(request.prompt());
        log.info("Comparison result: {}", result);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/chat")
    public ResponseEntity<ComparisonResult> chat(
            @Valid @RequestBody ChatRequest request
    ) {
        log.info("Received chat request with prompt: {}", request.prompt());
        ComparisonResult result = comparisonService.chat(request);
        log.info("Chat response: {}", result);
        return ResponseEntity.ok(result);
    }
}