package com.gauravthakur.ai.comparison.controller;

import com.gauravthakur.ai.comparison.dto.ChatComparisonRequest;
import com.gauravthakur.ai.comparison.dto.ChatRequest;
import com.gauravthakur.ai.comparison.dto.ComparisonResult;
import com.gauravthakur.ai.comparison.service.ComparisonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
        return ResponseEntity.ok(
                comparisonService.compare(request.prompt())
        );
    }

    @PostMapping("/chat")
    public ResponseEntity<ComparisonResult> chat(
            @Valid @RequestBody ChatRequest request
    ) {
        return ResponseEntity.ok(
                comparisonService.chat(request)
        );
    }
}