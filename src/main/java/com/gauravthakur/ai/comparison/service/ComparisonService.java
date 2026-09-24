package com.gauravthakur.ai.comparison.service;

import com.gauravthakur.ai.comparison.adapter.AiChatAdapter;
import com.gauravthakur.ai.comparison.dto.ChatRequest;
import com.gauravthakur.ai.comparison.dto.ComparisonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ComparisonService {

    private final List<AiChatAdapter> adapters;
    private final Executor executor;

    public ComparisonService(
            List<AiChatAdapter> adapters,
            @Qualifier("aiComparisonExecutor") Executor executor
    ) {
        this.adapters = List.copyOf(adapters);
        this.executor = executor;
    }

    public Map<String, ComparisonResult> compare(String prompt) {
        log.info("Starting comparison for prompt: {}", prompt);
        List<CompletableFuture<AdapterResult>> futures = adapters.stream()
                .map(adapter -> CompletableFuture.supplyAsync(
                        () -> execute(adapter, prompt),
                        executor
                ))
                .toList();

        Map<String, ComparisonResult> results = new LinkedHashMap<>();

        for (CompletableFuture<AdapterResult> future : futures) {
            AdapterResult adapterResult = future.join();

            results.put(
                    adapterResult.key(),
                    adapterResult.result()
            );
        }

        log.info("Finished comparison for prompt: {}", prompt);
        return results;
    }

    public ComparisonResult chat(ChatRequest request) {
        log.info("Starting chat with request: {}", request);
        AiChatAdapter adapter = findAdapter(request);

        ComparisonResult result = execute(adapter, request.prompt()).result();
        log.info("Finished chat with result: {}", result);
        return result;
    }

    private AdapterResult execute(
            AiChatAdapter adapter,
            String prompt
    ) {
        log.info("Starting execution for adapter: {}", adapter);
        long startTime = System.nanoTime();

        try {
            String message = adapter.chat(prompt);
            long durationMs = elapsedMilliseconds(startTime);

            AdapterResult adapterResult = new AdapterResult(
                    adapter.resultKey(),
                    ComparisonResult.success(
                            adapter.integration(),
                            adapter.provider(),
                            adapter.model(),
                            message,
                            durationMs
                    )
            );
            log.info("Finished execution for adapter: {} with result: {}", adapter, adapterResult);
            return adapterResult;
        } catch (Exception exception) {
            long durationMs = elapsedMilliseconds(startTime);

            AdapterResult adapterResult = new AdapterResult(
                    adapter.resultKey(),
                    ComparisonResult.failure(
                            adapter.integration(),
                            adapter.provider(),
                            adapter.model(),
                            durationMs,
                            exceptionMessage(exception)
                    )
            );
            log.info("Finished execution for adapter: {} with failure: {}", adapter, adapterResult);
            return adapterResult;
        }
    }

    private long elapsedMilliseconds(long startTime) {
        return TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - startTime
        );
    }

    private AiChatAdapter findAdapter(ChatRequest request) {
        return adapters.stream()
                .filter(adapter ->
                        adapter.integration() == request.integration()
                )
                .filter(adapter ->
                        adapter.provider() == request.provider()
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported AI route: "
                                + request.integration().value()
                                + "/"
                                + request.provider().value()
                ));
    }

    private String exceptionMessage(Exception exception) {
        if (exception.getMessage() == null ||
                exception.getMessage().isBlank()) {
            return exception.getClass().getSimpleName();
        }

        return exception.getMessage();
    }

    private record AdapterResult(
            String key,
            ComparisonResult result
    ) {}

}