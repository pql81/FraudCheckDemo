package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.CardResponse;
import com.pql.fraudcheck.dto.FraudCheckRequest;
import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.dto.TerminalLocationResponse;
import com.pql.fraudcheck.repository.FraudDetectedRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class TransFraudServiceTest {

    @Mock
    FraudDetectedRepository fraudDetectedRepository;

    @Mock
    DummyCardServiceCaller dummyCardServiceCaller;

    @Mock
    DummyTerminalServiceCaller dummyTerminalServiceCaller;

    @InjectMocks
    TransFraudService transFraudService;


    @Before
    public void setUp() {
        when(dummyCardServiceCaller.getCardLastLocation(any())).thenReturn(CompletableFuture.completedFuture(new CardResponse(1.001, 2.002)));
        when(dummyCardServiceCaller.getCardUsage(any(), any())).thenReturn(CompletableFuture.completedFuture(25));
        when(dummyTerminalServiceCaller.getTerminalLocation(any())).thenReturn(CompletableFuture.completedFuture(new TerminalLocationResponse(2.002, 1.001)));
    }

    @Test
    public void testCheckAllFraudRules() throws Exception {
        FraudCheckRequest request = new FraudCheckRequest();
        request.setAmount(new BigDecimal(10.4));
        request.setCurrency("EUR");
        request.setCardNumber("5555555555554444");
        request.setTerminalId("T001");
        request.setThreatScore(25);

        FraudCheckResponse response = transFraudService.checkAllFraudRules(request);

        assertNotNull(response.getRejectionStatus());
    }

    @Test
    public void testCheckAllFraudRulesFailure() throws Exception {
        FraudCheckRequest request = new FraudCheckRequest();
        request.setAmount(new BigDecimal(10.4));
        request.setCurrency("EUR");
        request.setCardNumber("5555555555554444");
        request.setTerminalId("T001");
        request.setThreatScore(25);

        FraudCheckResponse response = transFraudService.checkAllFraudRules(request);

        assertNotNull(response.getRejectionMessage());
    }
}
