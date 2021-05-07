package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.CardResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Service
public class DummyCardServiceCaller {

    @Value("${card.service.url}")
    String cardServiceUrl;

    @Autowired
    private RestTemplate restTemplate;


    @Async
    @CircuitBreaker(name="cardService", fallbackMethod="getCardUsageFallback")
    @Retry(name="cardService", fallbackMethod="getCardUsageFallback")
    public CompletableFuture<Integer> getCardUsage(String cardNumber, Integer lastHours) {
        // restTemplate to call external card service (not in same cluster)
        // this is a non existing service, circuit breaker falls back to a mocked version returning fixed values
        // GET <cardServiceUrl>/card/{number}/transactions?lastHours=lastHours

        String url = cardServiceUrl+"/card/{number}/transactions";

        Map<String, String> params = new HashMap<>();
        params.put("number", cardNumber);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("lastHours", lastHours);

        HttpEntity<?> entity = new HttpEntity<>(getHeaders());

        CompletableFuture<Integer> future = new CompletableFuture<>();

        try {
            log.info("Calling service::{}", builder.build().toUriString()); // do not log the card number
            Integer response = restTemplate.exchange(
                    builder.buildAndExpand(params).toUriString(),
                    HttpMethod.GET, entity,
                    Integer.class).getBody();

            future.complete(response);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    private CompletableFuture<Integer> getCardUsageFallback(String cardNumber, Integer lastHours, Throwable t) {
        // the use of the fallback is this case is demonstrative as it acts as a mocked service
        // in a real scenario this method should handle a fallback properly
        log.warn("CardService.getCardUsage() fallback called");

        Integer transNum;

        // for test purpose
        if (cardNumber.endsWith("31")) {
            transNum = 30;
        } else if (cardNumber.endsWith("32")) {
            transNum = 80;
        } else {
            transNum = 15;
        }

        return CompletableFuture.completedFuture(transNum);
    }

    @Async
    @CircuitBreaker(name="cardService", fallbackMethod="getCardLastLocationFallback")
    @Retry(name="cardService", fallbackMethod="getCardLastLocationFallback")
    public CompletableFuture<CardResponse> getCardLastLocation(String cardNumber) {
        // restTemplate to call external card service (not in same cluster)
        // this is a non existing service, circuit breaker falls back to a mocked version returning fixed values
        // GET <url:port>/card/{number}/last-location

        String url = cardServiceUrl+"/card/{number}/last-location";

        Map<String, String> params = new HashMap<>();
        params.put("number", cardNumber);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        HttpEntity<?> entity = new HttpEntity<>(getHeaders());

        CompletableFuture<CardResponse> future = new CompletableFuture<>();

        try {
            log.info("Calling service::{}", builder.build().toUriString()); // do not log the card number
            CardResponse response = restTemplate.exchange(
                    builder.buildAndExpand(params).toUriString(),
                    HttpMethod.GET, entity,
                    CardResponse.class).getBody();

            future.complete(response);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    private CompletableFuture<CardResponse> getCardLastLocationFallback(String cardNumber, Throwable t) {
        // the use of the fallback is this case is demonstrative as it acts as a mocked service
        // in a real scenario this method should handle a fallback properly
        log.warn("CardService.getCardLastLocation() fallback called");

        CardResponse response = new CardResponse(1232.111, 230.333);

        return CompletableFuture.completedFuture(response);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }
}
