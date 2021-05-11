package com.pql.fraudcheck.service;

import com.pql.fraudcheck.domain.FraudDetected;
import com.pql.fraudcheck.dto.FraudCheckRequest;
import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.repository.FraudDetectedRepository;
import org.jboss.logging.MDC;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class FraudDetectedServiceTest {

    @Mock
    FraudDetectedRepository fraudDetectedRepository;

    @InjectMocks
    FraudDetectedService fraudDetectedService;


    @Before
    public void setUp() {
        FraudDetected fraudToSave =new FraudDetected();
        fraudToSave.setId(123L);
        fraudToSave.setMaskedCardNumber("5***********4444");
        fraudToSave.setTerminalId("test123");

        when(fraudDetectedRepository.save(any())).thenReturn(fraudToSave);
    }

    @After
    public void tearDown() {
        MDC.clear();
    }

    @Test
    public void testSaveFraud() throws Exception {
        FraudCheckRequest checkReq = getFraudCheckRequestForTest();
        FraudCheckResponse checkResp = getFraudCheckResponseForTest();

        fraudDetectedService.saveFraud(checkReq, checkResp);

        Mockito.verify(fraudDetectedRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void testCreateFraudDetected() throws Exception {
        MDC.put("requestId", "MY-TEST"); // this is done in LogFilter

        FraudCheckRequest checkReq = getFraudCheckRequestForTest();
        FraudCheckResponse checkResp = getFraudCheckResponseForTest();

        FraudDetected result = fraudDetectedService.createFraudDetected(checkReq, checkResp);

        assertEquals("MY-TEST", result.getRequestId());
        assertEquals((Double)10.4, result.getAmount());
        assertEquals("EUR", result.getCurrency());
        assertEquals("5***********4444", result.getMaskedCardNumber());
        assertEquals("test123", result.getTerminalId());
        assertEquals(25, result.getThreatScore().intValue());
        assertEquals(30, result.getFraudScore().intValue());
        assertEquals("test1;test2", result.getRejectionMessage());
    }

    private FraudCheckRequest getFraudCheckRequestForTest() {
        FraudCheckRequest checkReq = new FraudCheckRequest();
        checkReq.setAmount(10.4);
        checkReq.setCurrency("EUR");
        checkReq.setCardNumber("5555555555554444");
        checkReq.setTerminalId("test123");
        checkReq.setThreatScore(25);

        return checkReq;
    }

    private FraudCheckResponse getFraudCheckResponseForTest() {
        return new FraudCheckResponse(FraudCheckResponse.RejStatus.REJECTED, "test1;test2", 30);
    }
}
