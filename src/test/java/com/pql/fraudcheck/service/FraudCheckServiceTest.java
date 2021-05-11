package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.CardResponse;
import com.pql.fraudcheck.dto.FraudCheckRequest;
import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.dto.TerminalLocationResponse;
import com.pql.fraudcheck.exception.CurrencyException;
import com.pql.fraudcheck.exception.TerminalException;
import com.pql.fraudcheck.rules.FraudRulesHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class FraudCheckServiceTest {

    @Mock
    FraudDetectedService fraudDetectedService;

    @Mock
    DummyCardServiceCaller dummyCardServiceCaller;

    @Mock
    DummyTerminalServiceCaller dummyTerminalServiceCaller;

    @Mock
    FraudRulesHandler fraudRulesHandler;

    @InjectMocks
    FraudCheckService fraudCheckService = new FraudCheckService(12, 48);


    @Before
    public void setUp() {
        when(fraudRulesHandler.checkIncomingTransaction(any())).thenReturn(new FraudCheckResponse(FraudCheckResponse.RejStatus.ALLOWED, "", 0));
        when(dummyCardServiceCaller.getCardLastLocation(anyString())).thenReturn(CompletableFuture.completedFuture(new CardResponse(1.001, 2.002)));
        when(dummyCardServiceCaller.getCardUsage(anyString(), anyInt())).thenReturn(CompletableFuture.completedFuture(25));
        when(dummyTerminalServiceCaller.getTerminalLocation(startsWith("T"))).thenReturn(CompletableFuture.completedFuture(new TerminalLocationResponse(2.002, 1.001)));
        when(dummyTerminalServiceCaller.getTerminalLastTransactions(startsWith("T"), anyInt())).thenReturn(CompletableFuture.completedFuture(145));
    }

    @Test
    public void testCheckAllFraudRulesAllow() throws Exception {
        FraudCheckRequest request = getFraudCheckRequestForTest();

        FraudCheckResponse response = fraudCheckService.checkAllFraudRules(request);

        Mockito.verify(dummyCardServiceCaller, Mockito.times(1)).getCardUsage(eq("5555555555554444"), eq(12));
        Mockito.verify(dummyTerminalServiceCaller, Mockito.times(1)).getTerminalLastTransactions(eq("T001"), eq(48));

        assertNotNull(response.getRejectionStatus());
        assertEquals(FraudCheckResponse.RejStatus.ALLOWED, response.getRejectionStatus());

        Mockito.verify(fraudDetectedService, Mockito.times(0)).saveFraud(any(), any());
    }

    @Test
    public void testCheckAllFraudRulesDeny() throws Exception {
        when(fraudRulesHandler.checkIncomingTransaction(any())).thenReturn(new FraudCheckResponse(FraudCheckResponse.RejStatus.DENIED, "Test", 10));

        FraudCheckRequest request = getFraudCheckRequestForTest();

        FraudCheckResponse response = fraudCheckService.checkAllFraudRules(request);

        Mockito.verify(dummyCardServiceCaller, Mockito.times(1)).getCardUsage(eq("5555555555554444"), eq(12));
        Mockito.verify(dummyTerminalServiceCaller, Mockito.times(1)).getTerminalLastTransactions(eq("T001"), eq(48));

        assertEquals(FraudCheckResponse.RejStatus.DENIED, response.getRejectionStatus());
        assertNotNull(response.getRejectionMessage());

        Mockito.verify(fraudDetectedService, Mockito.times(1)).saveFraud(any(), any());
    }

    @Test
    public void testCheckAllFraudRulesFail() throws Exception {
        CompletableFuture<TerminalLocationResponse> future1 = new CompletableFuture<>();
        future1.completeExceptionally(new TerminalException("test"));
        CompletableFuture<Integer> future2 = new CompletableFuture<>();
        future2.completeExceptionally(new TerminalException("test"));

        when(dummyTerminalServiceCaller.getTerminalLocation(startsWith("T"))).thenReturn(future1);
        when(dummyTerminalServiceCaller.getTerminalLastTransactions(startsWith("T"), any())).thenReturn(future2);
        when(fraudRulesHandler.handleInvalidTerminal()).thenReturn(new FraudCheckResponse(FraudCheckResponse.RejStatus.DENIED, "Test", 50));

        FraudCheckRequest request = getFraudCheckRequestForTest();

        FraudCheckResponse response = fraudCheckService.checkAllFraudRules(request);

        Mockito.verify(dummyCardServiceCaller, Mockito.times(1)).getCardUsage(eq("5555555555554444"), eq(12));
        Mockito.verify(dummyTerminalServiceCaller, Mockito.times(1)).getTerminalLastTransactions(eq("T001"), eq(48));

        assertEquals(FraudCheckResponse.RejStatus.DENIED, response.getRejectionStatus());
        assertNotNull(response.getRejectionMessage());

        Mockito.verify(fraudDetectedService, Mockito.times(1)).saveFraud(any(), any());
    }

    @Test(expected = CurrencyException.class)
    public void testCheckAllFraudRulesInvalidCurrency() throws Exception {
        FraudCheckRequest request = new FraudCheckRequest();
        request.setAmount(10.4);
        request.setCurrency("BBB");
        request.setCardNumber("5555555555554444");
        request.setTerminalId("T001");

        fraudCheckService.checkAllFraudRules(request);

        Mockito.verify(dummyCardServiceCaller, Mockito.times(0)).getCardUsage(anyString(), anyInt());
        Mockito.verify(dummyTerminalServiceCaller, Mockito.times(0)).getTerminalLastTransactions(anyString(), anyInt());

        Mockito.verify(fraudDetectedService, Mockito.times(0)).saveFraud(any(), any());
    }

    private FraudCheckRequest getFraudCheckRequestForTest() {
        FraudCheckRequest request = new FraudCheckRequest();
        request.setAmount(10.4);
        request.setCurrency("EUR");
        request.setCardNumber("5555555555554444");
        request.setTerminalId("T001");
        request.setThreatScore(25);

        return request;
    }
}
