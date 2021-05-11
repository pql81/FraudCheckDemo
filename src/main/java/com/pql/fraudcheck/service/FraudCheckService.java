package com.pql.fraudcheck.service;

import com.pql.fraudcheck.dto.*;
import com.pql.fraudcheck.exception.CurrencyException;
import com.pql.fraudcheck.exception.FraudCheckException;
import com.pql.fraudcheck.exception.TerminalException;
import com.pql.fraudcheck.rules.FraudRulesHandler;
import com.pql.fraudcheck.util.CardUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.money.Monetary;
import javax.money.UnknownCurrencyException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * Created by pasqualericupero on 05/05/2021.
 */
@Log4j2
@Service
public class FraudCheckService {

    @Autowired
    private DummyCardServiceCaller dummyCardServiceCaller;

    @Autowired
    private DummyTerminalServiceCaller dummyTerminalServiceCaller;

    @Autowired
    private FraudRulesHandler fraudRulesHandler;

    @Autowired
    private FraudDetectedService fraudDetectedService;

    private final int cardTransHoursRange;
    private final int terminalTransHoursRange;


    public FraudCheckService(@Value("${fraud.card.transactions.range.hours}") int cardTransHoursRange,
                             @Value("${fraud.terminal.transactions.range.hours}") int terminalTransHoursRange) {
        this.cardTransHoursRange = cardTransHoursRange;
        this.terminalTransHoursRange = terminalTransHoursRange;
    }

    public FraudCheckResponse checkAllFraudRules(FraudCheckRequest request) {
        log.info("Started fraud check for cardNumber::{} and terminalId::{}", CardUtil.getMaskedPan(request.getCardNumber()), request.getTerminalId());

        // why this check here? if currency is invalid then there is no need to proceed, return 422 and save time
        checkCurrency(request.getCurrency());

        FraudCheckResponse response;

        try {
            // invoke external services in parallel and wait for them to complete
            IncomingTransactionInfo transInfo = launchServiceRequests(request);

            // check rules
            response = fraudRulesHandler.checkIncomingTransaction(transInfo);

        } catch (CompletionException ce) {
            if (ce.getCause() instanceof TerminalException) {
                response = fraudRulesHandler.handleInvalidTerminal();
            } else {
                throw new FraudCheckException("Unable to verify the transaction", ce.getCause());
            }
        } catch (FraudCheckException | CurrencyException ce) {
            // fraudRulesHandler already managed the exception
            throw ce;
        } catch (Exception e) {
            log.error("Unexpected error", e);
            throw new RuntimeException("Unexpected error", e);
        }

        // if fraud is detected then save it to the DB
        if (response.getRejectionStatus() == FraudCheckResponse.RejStatus.REJECTED) {
            fraudDetectedService.saveFraud(request, response);
        }

        log.info("Fraud check for cardNumber::{} and terminalId::{} complete", CardUtil.getMaskedPan(request.getCardNumber()), request.getTerminalId());

        return response;
    }

    private IncomingTransactionInfo getTransInfo(FraudCheckRequest request, Integer cardUsage, CardResponse card, Integer terminalTrans, TerminalLocationResponse terminal) {
        return new IncomingTransactionInfo(
                request.getAmount(),
                request.getCurrency(),
                request.getThreatScore(),
                cardUsage,
                card.getLastLocationLat(),
                card.getLastLocationLong(),
                terminalTrans,
                terminal.getLatitude(),
                terminal.getLongitude()
        );
    }

    private void checkCurrency(String currency) {
        try {
            Monetary.getCurrency(currency);
        } catch (UnknownCurrencyException e) {
            throw new CurrencyException("Invalid currency ISO code", e);
        }
    }

    private IncomingTransactionInfo launchServiceRequests(FraudCheckRequest request) throws InterruptedException, ExecutionException {
        // call to external services in parallel in order to reduce general call time
        List<CompletableFuture> allFutures = new ArrayList<>();

        allFutures.add(0, dummyCardServiceCaller.getCardUsage(request.getCardNumber(), cardTransHoursRange));
        allFutures.add(1, dummyCardServiceCaller.getCardLastLocation(request.getCardNumber()));
        allFutures.add(2, dummyTerminalServiceCaller.getTerminalLastTransactions(request.getTerminalId(),terminalTransHoursRange));
        allFutures.add(3, dummyTerminalServiceCaller.getTerminalLocation(request.getTerminalId()));

        CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();

        Integer cardUsage = (Integer)allFutures.get(0).get();
        CardResponse cardResponse = (CardResponse)allFutures.get(1).get();
        Integer terminalTrans = (Integer)allFutures.get(2).get();
        TerminalLocationResponse terminalResponse = (TerminalLocationResponse)allFutures.get(3).get();

        return getTransInfo(request, cardUsage, cardResponse, terminalTrans, terminalResponse);
    }
}
