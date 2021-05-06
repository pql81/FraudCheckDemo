package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.FraudCheckRequest;
import com.pql.fraudcheck.dto.FraudCheckResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pasqualericupero on 05/05/2021.
 */
@Log4j2
@Service
public class TransFraudService {

    @Autowired
    DummyCardServiceCaller dummyCardServiceCaller;

    @Autowired
    DummyTerminalServiceCaller dummyTerminalServiceCaller;

    @Autowired
    FraudDetectedService fraudDetectedService;


    public FraudCheckResponse checkAllFraudRules(FraudCheckRequest request) {
        log.info("Started fraud check for cardNumber::* * * {} and terminalId::{}", request.getCardNumber().substring(12), request.getTerminalId());

        // call to external services in parallel in order to reduce general call time
        List<CompletableFuture> allFutures = new ArrayList<>();

        allFutures.add(dummyCardServiceCaller.getCardUsage(request.getCardNumber(), 24));
        allFutures.add(dummyCardServiceCaller.getCardLastLocation(request.getCardNumber()));
        allFutures.add(dummyTerminalServiceCaller.getTerminalLocation(request.getTerminalId()));

        CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();

        // check rules

        FraudCheckResponse response = new FraudCheckResponse(FraudCheckResponse.RejStatus.ALLOWED, null, 0);

        // if fraud is detected then save it to the DB
        if (response.getRejectionStatus() == FraudCheckResponse.RejStatus.DENIED) {
            fraudDetectedService.saveFraud(request, response);
        }

        log.info("Fraud check for cardNumber::{} and terminalId::{} complete", request.getCardNumber().substring(12), request.getTerminalId());

        return response;
    }
}
