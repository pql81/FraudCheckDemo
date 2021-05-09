package com.pql.fraudcheck.service;

import org.jboss.logging.MDC;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by pasqualericupero on 09/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceClientWithRetryTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    ServiceClientWithRetry serviceClientWithRetry;


    @Before
    public void setUp() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
                ArgumentMatchers.<Class<String>>any())).thenReturn(new ResponseEntity<>("TEST", HttpStatus.OK));
    }

    @After
    public void tearDown() {
        MDC.clear();
    }

    @Test
    public void testSendGetRequest() throws Exception {
        MDC.put("requestId", "TEST-123"); // this is done in LogFilter

        serviceClientWithRetry.sendGetRequest("http://test.io/test-again", String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set("correlation-id", "TEST-123");

        HttpEntity entity = new HttpEntity<>(null, headers);

        Mockito.verify(restTemplate, Mockito.times(1)).exchange(eq("http://test.io/test-again"), eq(HttpMethod.GET), eq(entity), eq(String.class));
    }

    @Test(expected = RuntimeException.class)
    public void testSendGetRequestException() throws Exception {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
                ArgumentMatchers.<Class<String>>any())).thenThrow(RuntimeException.class);

        serviceClientWithRetry.sendGetRequest("http://test.io/test-again", String.class);
    }
}
