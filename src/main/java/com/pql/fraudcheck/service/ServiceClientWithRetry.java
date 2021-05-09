package com.pql.fraudcheck.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by pasqualericupero on 08/05/2021.
 */
@Log4j2
@Component
public class ServiceClientWithRetry {

    private final static String REQUEST_ID_HEADER = "correlation-id";
    private final static String REQUEST_ID_MDC = "requestId";

    @Autowired
    private RestTemplate restTemplate;


    // This method is configured to retry on failure - see resilience4j.retry setting in application.yaml
    // Some exception won't trigger the retry, like HttpClientErrorException and ResourceAccessException
    // Extending this class prevents the retry to work properly in the child class, so it has to be injected
    @Retry(name="serviceClient")
    public <T> T sendGetRequest(String path, Class<T> responseClass) {
        return sendRequest(HttpMethod.GET, path, getHeaders(), null, responseClass);
    }

    private <T> T sendRequest(HttpMethod httpMethod, String path, HttpHeaders headers, Object request, Class<T> responseClass) {
        HttpEntity<?> entity = new HttpEntity<>(request, headers);

        try {
            Instant start = Instant.now();
            ResponseEntity<T> response = restTemplate.exchange(
                    path,
                    httpMethod,
                    entity,
                    responseClass);

            Instant finish = Instant.now();

            log.info("Service call took {}ms", Duration.between(start, finish).toMillis());

            return response.getBody();

        } catch(Exception e) {
            // not logging the stacktrace here because of the retry - could be too verbose
            log.debug("Attempt to call service failed");
            throw e;
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set(REQUEST_ID_HEADER, MDC.get(REQUEST_ID_MDC));

        return headers;
    }
}
