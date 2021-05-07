package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.TerminalLocationResponse;
import com.pql.fraudcheck.exception.TerminalException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;;import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Service
public class DummyTerminalServiceCaller {

    @Value("${transaction.service.url}")
    String transactionServiceUrl;

    @Autowired
    private RestTemplate restTemplate;


    @Async
    @CircuitBreaker(name="terminalService", fallbackMethod="getTerminalLastTransactionsFallback")
    @Retry(name="terminalService", fallbackMethod="getTerminalLastTransactionsFallback")
    public CompletableFuture<Integer> getTerminalLastTransactions(String terminalId, Integer lastHours) {
        // restTemplate to call terminal service (possibly in same cluster)
        // this is a non existing service, circuit breaker falls back to a mocked version returning fixed values
        // GET <terminalServiceUrl>/terminal/{terminalId}/transactions

        String url = transactionServiceUrl+"/terminal/{terminalId}/transactions";

        Map<String, String> params = new HashMap<>();
        params.put("terminalId", terminalId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("lastHours", lastHours);

        HttpEntity<?> entity = new HttpEntity<>(getHeaders());

        CompletableFuture<Integer> future = new CompletableFuture<>();

        try {
            log.info("Calling service::{}", builder.build().toUriString());
            ResponseEntity<Integer> response = restTemplate.exchange(
                    builder.buildAndExpand(params).toUriString(),
                    HttpMethod.GET, entity,
                    Integer.class);

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                future.completeExceptionally(new TerminalException("Not Found"));
            } else {
                future.complete(response.getBody());
            }
        } catch (Exception e) {
            // placing a log here causes unexpected behaviour
            future.completeExceptionally(e);
        }

        return future;
    }

    private CompletableFuture<Integer> getTerminalLastTransactionsFallback(String terminalId, Integer lastHours, Throwable t) {
        // the use of the fallback is this case is demonstrative as it acts as a mocked service
        // in a real scenario this method should handle a fallback properly
        log.warn("TerminalService.getTerminalLastTransactions() fallback called");

        CompletableFuture<Integer> future = new CompletableFuture<>();

        if (!terminalId.startsWith("T")) {
            future.completeExceptionally(new TerminalException("Not Found"));
        } else {
            Integer transNum;
            // for test purpose
            if (terminalId.endsWith("02")) {
                transNum = 697;
            } else if (terminalId.endsWith("03")) {
                transNum = 980;
            } else {
                transNum = 154;
            }
            future.complete(transNum);
        }

        return future;
    }

    @Async
    @CircuitBreaker(name="terminalService", fallbackMethod="getTerminalLocationFallback")
    @Retry(name="terminalService", fallbackMethod="getTerminalLocationFallback")
    public CompletableFuture<TerminalLocationResponse> getTerminalLocation(String terminalId) {
        // restTemplate to call terminal service (possibly in same cluster)
        // this is a non existing service, circuit breaker falls back to a mocked version returning fixed values
        // GET <terminalServiceUrl>/terminal/{terminalId}/last-location

        String url = transactionServiceUrl+"/terminal/{terminalId}/last-location";

        Map<String, String> params = new HashMap<>();
        params.put("terminalId", terminalId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        HttpEntity<?> entity = new HttpEntity<>(getHeaders());

        CompletableFuture<TerminalLocationResponse> future = new CompletableFuture<>();

        try {
            log.info("Calling service::{}", builder.build().toUriString());
            ResponseEntity<TerminalLocationResponse> response = restTemplate.exchange(
                    builder.buildAndExpand(params).toUriString(),
                    HttpMethod.GET, entity,
                    TerminalLocationResponse.class);

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                future.completeExceptionally(new TerminalException("Not Found"));
            } else {
                future.complete(response.getBody());
            }
        } catch (Exception e) {
            // placing a log here causes unexpected behaviour
            future.completeExceptionally(e);
        }

        return future;
    }

    private CompletableFuture<TerminalLocationResponse> getTerminalLocationFallback(String terminalId, Throwable t) {
        // the use of the fallback is this case is demonstrative as it acts as a mocked service
        // in a real scenario this method should handle a fallback properly
        log.warn("TerminalService.getTerminalLocation() fallback called");

        CompletableFuture<TerminalLocationResponse> future = new CompletableFuture<>();

        if (!terminalId.startsWith("T")) {
            future.completeExceptionally(new TerminalException("Not Found"));
        } else {
            future.complete(new TerminalLocationResponse(1234.567, 234.987));
        }

        return future;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }
}
