package com.pql.fraudcheck.service;

import com.pql.fraudcheck.domain.FraudDetected;
import com.pql.fraudcheck.dto.FraudCheckRequest;
import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.repository.FraudDetectedRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

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
        fraudToSave.setLastCardDigits("4444");
        fraudToSave.setTerminalId("test123");

        when(fraudDetectedRepository.save(any())).thenReturn(fraudToSave);
    }

    @Test
    public void testSaveFraud() throws Exception {
        FraudCheckRequest checkReq = new FraudCheckRequest();
        checkReq.setAmount(10.4);
        checkReq.setCurrency("EUR");
        checkReq.setCardNumber("5555555555554444");
        checkReq.setTerminalId("test123");
        checkReq.setThreatScore(25);

        FraudCheckResponse checkResp = new FraudCheckResponse(FraudCheckResponse.RejStatus.ALLOWED, null, 0);

        fraudDetectedService.saveFraud(checkReq, checkResp);

        Mockito.verify(fraudDetectedRepository, Mockito.times(1)).save(any());
    }
}
