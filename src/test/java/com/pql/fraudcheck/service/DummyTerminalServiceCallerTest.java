package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.TerminalLocationResponse;
import com.pql.fraudcheck.exception.TerminalException;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class DummyTerminalServiceCallerTest {

    @Mock
    private ServiceClientWithRetry serviceClientWithRetry;

    @InjectMocks
    DummyTerminalServiceCaller dummyTerminalServiceCaller = new DummyTerminalServiceCaller("http://test.io");


    @Before
    public void setUp() {
        when(serviceClientWithRetry.sendGetRequest(contains("transactions"), ArgumentMatchers.<Class<Integer>>any()))
                .thenReturn(145);
        when(serviceClientWithRetry.sendGetRequest(contains("last-location"), ArgumentMatchers.<Class<TerminalLocationResponse>>any()))
                .thenReturn(new TerminalLocationResponse(2.002, 1.001));
    }

    @Test
    public void testGetTerminalLocation() throws Exception {
        CompletableFuture<TerminalLocationResponse> response = dummyTerminalServiceCaller.getTerminalLocation("test01");

        assertNotNull(response.get());
        assertEquals((Double)2.002, response.get().getLatitude());
        assertEquals((Double)1.001, response.get().getLongitude());
    }

    @Test(expected = RuntimeException.class)
    public void testGetTerminalLocationFailure() throws Throwable {
        when(serviceClientWithRetry.sendGetRequest(anyString(), ArgumentMatchers.<Class<TerminalLocationResponse>>any()))
                .thenThrow(RuntimeException.class);

        CompletableFuture<TerminalLocationResponse> response = dummyTerminalServiceCaller.getTerminalLocation("test01");

        try {
            response.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = TerminalException.class)
    public void testGetTerminalLocationNotFound() throws Throwable {
        when(serviceClientWithRetry.sendGetRequest(anyString(), ArgumentMatchers.<Class<TerminalLocationResponse>>any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        CompletableFuture<TerminalLocationResponse> response = dummyTerminalServiceCaller.getTerminalLocation("test01");

        try {
            response.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetTerminalLastTransactions() throws Exception {
        CompletableFuture<Integer> response = dummyTerminalServiceCaller.getTerminalLastTransactions("test01", 24);

        assertNotNull(response.get());
        assertEquals((Integer)145, response.get());
    }

    @Test(expected = RuntimeException.class)
    public void testGetTerminalLastTransactionsFailure() throws Throwable {
        when(serviceClientWithRetry.sendGetRequest(anyString(), ArgumentMatchers.<Class<Integer>>any()))
                .thenThrow(RuntimeException.class);

        CompletableFuture<Integer> response = dummyTerminalServiceCaller.getTerminalLastTransactions("test01", 24);

        try {
            response.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = TerminalException.class)
    public void testGetTerminalLastTransactionsNotFound() throws Throwable {
        when(serviceClientWithRetry.sendGetRequest(anyString(), ArgumentMatchers.<Class<Integer>>any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        CompletableFuture<Integer> response = dummyTerminalServiceCaller.getTerminalLastTransactions("test01", 24);

        try {
            response.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
}
