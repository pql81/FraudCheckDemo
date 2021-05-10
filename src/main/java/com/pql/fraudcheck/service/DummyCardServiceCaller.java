package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.CardResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
public class DummyCardServiceCaller {

    @Autowired
    private ServiceClientWithRetry serviceClientWithRetry;

    @Autowired
    private SimpleEncryptionService simpleEncryptionService;

    private final String cardServiceUrl;
    private final String cardUsagePath = "/card/{number}/transactions";
    private final String cardLocationPath = "/card/{number}/last-location";


    public DummyCardServiceCaller(@Value("${card.service.url}") String cardServiceUrl) {
        this.cardServiceUrl = cardServiceUrl;
    }

    @Async
    @CircuitBreaker(name="cardService", fallbackMethod="getCardUsageFallback")
    public CompletableFuture<Integer> getCardUsage(String cardNumber, Integer lastHours) {
        // restTemplate to call external card service (not in same cluster)
        // this is a non existing service, circuit breaker falls back to a mocked version returning fixed values
        // GET <cardServiceUrl>/card/{number}/transactions?lastHours=lastHours

        log.info("CardService.getCardUsage() called");

        String url = cardServiceUrl + cardUsagePath;

        Map<String, String> params = new HashMap<>();
        params.put("number", simpleEncryptionService.encrypt(cardNumber));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("lastHours", lastHours);

        CompletableFuture<Integer> future = new CompletableFuture<>();

        try {
            Integer response = serviceClientWithRetry.sendGetRequest(builder.buildAndExpand(params).toUriString(), Integer.class);
            future.complete(response);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // assuming 404 in this case means no recent transaction found for this card
                future.complete(0);
            } else {
                future.completeExceptionally(e);
            }
        } catch (Exception e) {
            log.warn("CardService.getCardUsage() failed");
            future.completeExceptionally(e);
        }

        return future;
    }

    private CompletableFuture<Integer> getCardUsageFallback(String cardNumber, Integer lastHours, Throwable t) {
        // the use of the fallback is this case is demonstrative as it acts as a mocked service
        // in a real scenario this method should handle a fallback properly
        log.warn("CardService.getCardUsage() fallback called");

        CompletableFuture<Integer> future = new CompletableFuture<>();

        if (t instanceof HttpClientErrorException) {
            future.completeExceptionally(t);
        } else {
            Integer transNum;

            // for test purpose
            if (cardNumber.endsWith("31")) {
                transNum = 30;
            } else if (cardNumber.endsWith("32")) {
                transNum = 80;
            } else {
                transNum = 15;
            }

            future.complete(transNum);
        }

        return future;
    }

    @Async
    @CircuitBreaker(name="cardService", fallbackMethod="getCardLastLocationFallback")
    public CompletableFuture<CardResponse> getCardLastLocation(String cardNumber) {
        // restTemplate to call external card service (not in same cluster)
        // this is a non existing service, circuit breaker falls back to a mocked version returning fixed values
        // GET <url:port>/card/{number}/last-location

        log.info("CardService.getCardLastLocation() called");

        String url = cardServiceUrl + cardLocationPath;

        Map<String, String> params = new HashMap<>();
        params.put("number", simpleEncryptionService.encrypt(cardNumber));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        CompletableFuture<CardResponse> future = new CompletableFuture<>();

        try {
            CardResponse response = serviceClientWithRetry.sendGetRequest(builder.buildAndExpand(params).toUriString(), CardResponse.class);
            future.complete(response);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // no geolocation available - setting null values
                future.complete(new CardResponse(null, null));
            } else {
                future.completeExceptionally(e);
            }
        } catch (Exception e) {
            log.warn("CardService.getCardLastLocation() failed");
            future.completeExceptionally(e);
        }

        return future;
    }

    private CompletableFuture<CardResponse> getCardLastLocationFallback(String cardNumber, Throwable t) {
        // the use of the fallback is this case is demonstrative as it acts as a mocked service
        // in a real scenario this method should handle a fallback properly
        log.warn("CardService.getCardLastLocation() fallback called");

        CompletableFuture<CardResponse> future = new CompletableFuture<>();

        if (t instanceof HttpClientErrorException) {
            future.completeExceptionally(t);
        } else {
            CardResponse response = new CardResponse(-1.276, 2.315);

            future.complete(response);
        }

        return future;
    }
}
