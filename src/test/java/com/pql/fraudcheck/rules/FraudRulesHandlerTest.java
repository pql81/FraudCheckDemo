package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.FraudCheckException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class FraudRulesHandlerTest {

    FraudRulesHandler fraudRulesHandler;

    Map<String, IFraudDetection> fraudRuleMap = new HashMap<>();

    @Mock
    IFraudDetection dummyRule1, dummyRule2, dummyRule3;


    @Before
    public void setUp() {
        fraudRuleMap.put("DUMMY1", dummyRule1);
        fraudRuleMap.put("DUMMY2", dummyRule2);
        fraudRuleMap.put("DUMMY3", dummyRule3);

        fraudRuleMap.entrySet().forEach(e -> when(e.getValue().isEnabled()).thenReturn(true));
        when(dummyRule1.checkFraud(any())).thenReturn(new FraudRuleScore(0, null));
        when(dummyRule2.checkFraud(any())).thenReturn(new FraudRuleScore(50, "test1"));
        when(dummyRule3.checkFraud(any())).thenReturn(new FraudRuleScore(20, "test2"));

        fraudRulesHandler = new FraudRulesHandler(fraudRuleMap);
    }

    @Test
    public void testCheckIncomingTransactionAllowed() throws Exception {
        fraudRuleMap.entrySet().forEach(e -> when(e.getValue().checkFraud(any())).thenReturn(new FraudRuleScore(0, null)));

        IncomingTransactionInfo transInfo = new IncomingTransactionInfo(200.00, "EUR", 14, 20, 1.234, 1.234, 80, 1.11, 1.22);

        FraudCheckResponse response = fraudRulesHandler.checkIncomingTransaction(transInfo);
        assertEquals(FraudCheckResponse.RejStatus.ALLOWED, response.getRejectionStatus());
        assertEquals(0, response.getFraudScore().intValue());
        assertNull(response.getRejectionMessage());

        fraudRuleMap.entrySet().stream().forEach(rule ->
                Mockito.verify(fraudRuleMap.get(rule.getKey()), Mockito.times(1)).checkFraud(any()));
    }

    @Test
    public void testCheckIncomingTransactionDenied() throws Exception {
        IncomingTransactionInfo transInfo = new IncomingTransactionInfo(200.00, "EUR", 14, 20, 1.234, 1.234, 80, 1.11, 1.22);

        FraudCheckResponse response = fraudRulesHandler.checkIncomingTransaction(transInfo);
        assertEquals(FraudCheckResponse.RejStatus.REJECTED, response.getRejectionStatus());
        assertEquals(70,response.getFraudScore().intValue());
        assertTrue(response.getRejectionMessage().contains("test1"));
        assertTrue(response.getRejectionMessage().contains("test2"));

        fraudRuleMap.entrySet().stream().forEach(rule ->
                Mockito.verify(fraudRuleMap.get(rule.getKey()), Mockito.times(1)).checkFraud(any()));
    }

    @Test
    public void testCheckIncomingTransactionRuleDisabled() throws Exception {
        when(dummyRule2.isEnabled()).thenReturn(false);
        when(dummyRule3.isEnabled()).thenReturn(false);

        // need to create a new FraudRulesHandler object to load a different rule map in the constructor
        FraudRulesHandler fraudRulesHandlerOneRule = new FraudRulesHandler(fraudRuleMap);

        IncomingTransactionInfo transInfo = new IncomingTransactionInfo(200.00, "EUR", 14, 20, 1.234, 1.234, 80, 1.11, 1.22);

        FraudCheckResponse response = fraudRulesHandlerOneRule.checkIncomingTransaction(transInfo);
        assertEquals(FraudCheckResponse.RejStatus.ALLOWED, response.getRejectionStatus());
        assertEquals(0, response.getFraudScore().intValue());
        assertNull(response.getRejectionMessage());

        fraudRuleMap.entrySet().stream().forEach(rule -> {
                int times = rule.getValue() == dummyRule1 ? 1 : 0;
                Mockito.verify(fraudRuleMap.get(rule.getKey()), Mockito.times(times)).checkFraud(any());
        });
    }

    @Test(expected = FraudCheckException.class)
    public void testCheckIncomingTransactionError() throws Exception {
        when(dummyRule3.checkFraud(any())).thenThrow(new ArithmeticException());

        IncomingTransactionInfo transInfo = new IncomingTransactionInfo(200.00, "EUR", 101, 20, 1.234, 1.234, 80, 1.11, 1.22);

        fraudRulesHandler.checkIncomingTransaction(transInfo);
    }

    @Test
    public void testHandleInvalidTerminal() throws Exception {
        FraudCheckResponse response = fraudRulesHandler.handleInvalidTerminal();

        assertEquals(FraudCheckResponse.RejStatus.REJECTED, response.getRejectionStatus());
        assertEquals(100, response.getFraudScore().intValue());
        assertNotNull(response.getRejectionMessage());
    }
}
