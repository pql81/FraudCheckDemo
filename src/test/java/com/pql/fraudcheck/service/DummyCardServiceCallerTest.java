package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.CardResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class DummyCardServiceCallerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    DummyCardServiceCaller dummyCardServiceCaller;


    @Before
    public void setUp() {
        dummyCardServiceCaller.cardServiceUrl = "http://test.io";

        when(restTemplate.exchange(contains("transactions"), any(HttpMethod.class), any(),
                ArgumentMatchers.<Class<Integer>>any())).thenReturn(new ResponseEntity(25, HttpStatus.OK));
        when(restTemplate.exchange(contains("last-location"), any(HttpMethod.class), any(),
                ArgumentMatchers.<Class<CardResponse>>any())).thenReturn(new ResponseEntity(new CardResponse(1232.111, 230.333), HttpStatus.OK));
    }

    @Test
    public void testGetCardUsage() throws Exception {
        CompletableFuture<Integer> response = dummyCardServiceCaller.getCardUsage("5555555555554444", 24);

        assertNotNull(response.get());
        assertEquals((Integer)25, response.get());
    }

    @Test(expected = RuntimeException.class)
    public void testGetCardUsageFailure() throws Throwable {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
                ArgumentMatchers.<Class<String>>any())).thenThrow(RuntimeException.class);

        CompletableFuture<Integer> response = dummyCardServiceCaller.getCardUsage("5555555555554444", 24);

        try {
            response.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetCardLastLocation() throws Exception {
        CompletableFuture<CardResponse> response = dummyCardServiceCaller.getCardLastLocation("5555555555554444");

        assertNotNull(response.get());
        assertEquals((Double)1232.111, response.get().getLastLocationLat());
        assertEquals((Double)230.333, response.get().getLastLocationLong());
    }

    @Test(expected = RuntimeException.class)
    public void testGetCardLastLocationFailure() throws Throwable {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
                ArgumentMatchers.<Class<String>>any())).thenThrow(RuntimeException.class);

        CompletableFuture<CardResponse> response = dummyCardServiceCaller.getCardLastLocation("5555555555554444");

        try {
            response.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
}
