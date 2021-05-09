package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.TerminalLocationResponse;
import com.pql.fraudcheck.exception.TerminalException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Service
public class DummyTerminalServiceCaller {

    @Value("${terminal.service.url}")
    private String terminalServiceUrl;

    @Autowired
    private ServiceClientWithRetry serviceClientWithRetry;


    @Async
    @CircuitBreaker(name="terminalService", fallbackMethod="getTerminalLastTransactionsFallback")
    public CompletableFuture<Integer> getTerminalLastTransactions(String terminalId, Integer lastHours) {
        // restTemplate to call terminal service (possibly in same cluster)
        // this is a non existing service, circuit breaker falls back to a mocked version returning fixed values
        // GET <terminalServiceUrl>/terminal/{terminalId}/transactions

        log.info("TerminalService.getTerminalLastTransactions() called");

        String url = terminalServiceUrl+"/terminal/{terminalId}/transactions";

        Map<String, String> params = new HashMap<>();
        params.put("terminalId", terminalId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("lastHours", lastHours);

        CompletableFuture<Integer> future = new CompletableFuture<>();

        try {
            Integer response = serviceClientWithRetry.sendGetRequest(
                    builder.buildAndExpand(params).toUriString(),
                    Integer.class);

            future.complete(response);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                future.completeExceptionally(new TerminalException("Not Found"));
            } else {
                future.completeExceptionally(e);
            }
        } catch (Exception e) {
            log.warn("TerminalService.getTerminalLastTransactions() failed");
            future.completeExceptionally(e);
        }

        return future;
    }

    private CompletableFuture<Integer> getTerminalLastTransactionsFallback(String terminalId, Integer lastHours, Throwable t) {
        // the use of the fallback is this case is demonstrative as it acts as a mocked service
        // in a real scenario this method should handle a fallback properly
        log.warn("TerminalService.getTerminalLastTransactions() fallback called");

        CompletableFuture<Integer> future = new CompletableFuture<>();

        if (t instanceof TerminalException || !terminalId.startsWith("T")) {
            future.completeExceptionally(new TerminalException("Not Found"));
        } else {
            Integer transNum;
            // for test purpose
            if (terminalId.endsWith("02")) {
                transNum = 497;
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
    public CompletableFuture<TerminalLocationResponse> getTerminalLocation(String terminalId) {
        // restTemplate to call terminal service (possibly in same cluster)
        // this is a non existing service, circuit breaker falls back to a mocked version returning fixed values
        // GET <terminalServiceUrl>/terminal/{terminalId}/last-location

        log.info("TerminalService.getTerminalLocation() called");

        String url = terminalServiceUrl+"/terminal/{terminalId}/last-location";

        Map<String, String> params = new HashMap<>();
        params.put("terminalId", terminalId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        CompletableFuture<TerminalLocationResponse> future = new CompletableFuture<>();

        try {
            TerminalLocationResponse response = serviceClientWithRetry.sendGetRequest(
                    builder.buildAndExpand(params).toUriString(),
                    TerminalLocationResponse.class);

            future.complete(response);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                future.completeExceptionally(new TerminalException("Not Found"));
            } else {
                future.completeExceptionally(e);
            }
        } catch (Exception e) {
            log.warn("TerminalService.getTerminalLocation() failed");
            future.completeExceptionally(e);
        }

        return future;
    }

    private CompletableFuture<TerminalLocationResponse> getTerminalLocationFallback(String terminalId, Throwable t) {
        // the use of the fallback is this case is demonstrative as it acts as a mocked service
        // in a real scenario this method should handle a fallback properly
        log.warn("TerminalService.getTerminalLocation() fallback called");

        CompletableFuture<TerminalLocationResponse> future = new CompletableFuture<>();

        if (t instanceof TerminalException || !terminalId.startsWith("T")) {
            future.completeExceptionally(new TerminalException("Not Found"));
        } else {
            future.complete(new TerminalLocationResponse(-1.284, 2.324));
        }

        return future;
    }
}
