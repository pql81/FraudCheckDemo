package com.pql.fraudcheck.service;

import com.pql.fraudcheck.domain.FraudDetected;
import com.pql.fraudcheck.dto.FraudCheckRequest;
import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.repository.FraudDetectedRepository;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Service
public class FraudDetectedService {

    private final static String REQUEST_ID_MDC = "requestId";

    @Autowired
    private FraudDetectedRepository fraudDetectedRepository;


    @Async
    public void saveFraud(FraudCheckRequest request, FraudCheckResponse response) {
        FraudDetected fraud = fraudDetectedRepository.save(createFraudDetected(request, response));

        log.info("Detected fraud saved to DB with id::{}", fraud.getId());
    }

    FraudDetected createFraudDetected(FraudCheckRequest request, FraudCheckResponse response) {
        FraudDetected fraud = new FraudDetected();
        fraud.setRequestId(MDC.get(REQUEST_ID_MDC));
        fraud.setAmount(request.getAmount());
        fraud.setCurrency(request.getCurrency());
        fraud.setTerminalId(request.getTerminalId());
        fraud.setThreatScore(request.getThreatScore());
        fraud.setLastCardDigits(request.getCardNumber().substring(12));
        fraud.setRejectionMessage(response.getRejectionMessage());
        fraud.setFraudScore(response.getFraudScore());

        return fraud;
    }
}
