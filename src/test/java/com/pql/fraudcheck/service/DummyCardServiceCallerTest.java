package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.CardResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class DummyCardServiceCallerTest {

    @Mock
    private ServiceClientWithRetry serviceClientWithRetry;

    @Mock
    private SimpleEncryptionService simpleEncryptionService;

    @InjectMocks
    DummyCardServiceCaller dummyCardServiceCaller = new DummyCardServiceCaller("http://test.io");


    @Before
    public void setUp() {
        when(serviceClientWithRetry.sendGetRequest(contains("transactions"), ArgumentMatchers.<Class<Integer>>any()))
                .thenReturn(25);
        when(serviceClientWithRetry.sendGetRequest(contains("last-location"), ArgumentMatchers.<Class<CardResponse>>any()))
                .thenReturn(new CardResponse(1232.111, 230.333));
    }

    @Test
    public void testGetCardUsage() throws Exception {
        CompletableFuture<Integer> response = dummyCardServiceCaller.getCardUsage("5555555555554444", 24);

        assertNotNull(response.get());
        assertEquals(25, response.get().intValue());
    }

    @Test
    public void testGetCardUsageNotFound() throws Throwable {
        when(serviceClientWithRetry.sendGetRequest(anyString(), ArgumentMatchers.<Class<String>>any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        CompletableFuture<Integer> response = dummyCardServiceCaller.getCardUsage("5555555555554444", 24);

        assertNotNull(response.get());
        assertEquals(0, response.get().intValue());
    }

    @Test(expected = RuntimeException.class)
    public void testGetCardUsageFailure() throws Throwable {
        when(serviceClientWithRetry.sendGetRequest(anyString(), ArgumentMatchers.<Class<String>>any()))
                .thenThrow(RuntimeException.class);

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

    @Test
    public void testGetCardLastLocationNotFound() throws Throwable {
        when(serviceClientWithRetry.sendGetRequest(anyString(), ArgumentMatchers.<Class<String>>any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        CompletableFuture<CardResponse> response = dummyCardServiceCaller.getCardLastLocation("5555555555554444");

        assertNotNull(response.get());
        assertNull(response.get().getLastLocationLat());
        assertNull(response.get().getLastLocationLong());
    }

    @Test(expected = RuntimeException.class)
    public void testGetCardLastLocationFailure() throws Throwable {
        when(serviceClientWithRetry.sendGetRequest(anyString(), ArgumentMatchers.<Class<String>>any()))
                .thenThrow(RuntimeException.class);

        CompletableFuture<CardResponse> response = dummyCardServiceCaller.getCardLastLocation("5555555555554444");

        try {
            response.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
}
