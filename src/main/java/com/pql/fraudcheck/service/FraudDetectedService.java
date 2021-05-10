package com.pql.fraudcheck.service;

import com.pql.fraudcheck.domain.FraudDetected;
import com.pql.fraudcheck.dto.FraudCheckRequest;
import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.repository.FraudDetectedRepository;
import com.pql.fraudcheck.util.CardUtil;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Service
public class FraudDetectedService {

    private final static String REQUEST_ID_MDC = "requestId";

    @Autowired
    private FraudDetectedRepository fraudDetectedRepository;


    public List<FraudDetected> listFrauds() {
        Iterable<FraudDetected> frauds = fraudDetectedRepository.findAll();

        List<FraudDetected> response = StreamSupport
                .stream(frauds.spliterator(), false)
                .collect(Collectors.toList());

        return response;
    }

    public FraudDetected getFraud(String requestId) {
        Optional<FraudDetected> fraud = fraudDetectedRepository.findByRequestId(requestId);

        FraudDetected response = null;

        if (fraud.isPresent()) {
            response = fraud.get();
        } else {
            log.warn("Detected fraud not found - reference: {}", requestId);
        }

        return response;
    }

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
        fraud.setMaskedCardNumber(CardUtil.getMaskedPan(request.getCardNumber()));
        fraud.setRejectionMessage(response.getRejectionMessage());
        fraud.setFraudScore(response.getFraudScore());

        return fraud;
    }
}
