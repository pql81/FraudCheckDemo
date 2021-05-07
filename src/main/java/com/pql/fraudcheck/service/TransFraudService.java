package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.*;
import com.pql.fraudcheck.exception.CurrencyException;
import com.pql.fraudcheck.exception.FraudCheckException;
import com.pql.fraudcheck.exception.TerminalException;
import com.pql.fraudcheck.rules.FraudRulesHandler;
import com.pql.fraudcheck.util.CurrencyThreshold;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Created by pasqualericupero on 05/05/2021.
 */
@Log4j2
@Service
public class TransFraudService {

    @Autowired
    private DummyCardServiceCaller dummyCardServiceCaller;

    @Autowired
    private DummyTerminalServiceCaller dummyTerminalServiceCaller;

    @Autowired
    private FraudRulesHandler fraudRulesHandler;

    @Autowired
    private FraudDetectedService fraudDetectedService;


    public FraudCheckResponse checkAllFraudRules(FraudCheckRequest request) {
        log.info("Started fraud check for cardNumber::* * * {} and terminalId::{}", request.getCardNumber().substring(12), request.getTerminalId());

        // if currency is invalid there is no need to proceed
        checkCurrency(request.getCurrency());

        FraudCheckResponse response = null;

        // call to external services in parallel in order to reduce general call time
        List<CompletableFuture> allFutures = new ArrayList<>();

        allFutures.add(dummyCardServiceCaller.getCardUsage(request.getCardNumber(), 24));
        allFutures.add(dummyCardServiceCaller.getCardLastLocation(request.getCardNumber()));
        allFutures.add(dummyTerminalServiceCaller.getTerminalLastTransactions(request.getTerminalId(),24));
        allFutures.add(dummyTerminalServiceCaller.getTerminalLocation(request.getTerminalId()));

        try {
            CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();

            Integer cardUsage = (Integer)allFutures.get(0).get();
            CardResponse cardResponse = (CardResponse)allFutures.get(1).get();
            Integer terminalTrans = (Integer)allFutures.get(2).get();
            TerminalLocationResponse terminalResponse = (TerminalLocationResponse)allFutures.get(3).get();

            // check rules
            IncomingTransactionInfo transInfo = getTransInfo(request, cardUsage, cardResponse, terminalResponse);
            response = fraudRulesHandler.checkIncomingTransaction(transInfo);

        } catch (CompletionException e) {
            if (e.getCause() instanceof TerminalException) {
                response = fraudRulesHandler.handleInvalidTerminal(request.getThreatScore());
            } else {
                throw new FraudCheckException("Unable to verify the transaction", e.getCause());
            }
        } catch (Exception e) {
            log.error("Unexpected error", e);
            throw new RuntimeException("Unexpected error", e);
        }

        // if fraud is detected then save it to the DB
        if (response.getRejectionStatus() == FraudCheckResponse.RejStatus.DENIED) {
            fraudDetectedService.saveFraud(request, response);
        }

        log.info("Fraud check for cardNumber::{} and terminalId::{} complete", request.getCardNumber().substring(12), request.getTerminalId());

        return response;
    }

    private IncomingTransactionInfo getTransInfo(FraudCheckRequest request, Integer cardUsage, CardResponse card, TerminalLocationResponse terminal) {
        return new IncomingTransactionInfo(
                request.getAmount(),
                request.getCurrency(),
                request.getThreatScore(),
                cardUsage,
                card.getLastLocationLat(),
                card.getLastLocationLong(),
                terminal.getLatitude(),
                terminal.getLongitude()
        );
    }

    private void checkCurrency(String currency) {
        if (!EnumUtils.isValidEnum(CurrencyThreshold.class, currency)) {
            throw new CurrencyException("Currency invalid/not supported");
        }
    }
}
