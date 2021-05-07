package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.FraudCheckException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Component
public class FraudRulesHandler {

    private final Map<String, IFraudDetection> fraudRuleMap;


    public FraudRulesHandler(Map<String, IFraudDetection> fraudRuleMap) {
        this.fraudRuleMap = fraudRuleMap;
    }

    public FraudCheckResponse checkIncomingTransaction(IncomingTransactionInfo transInfo) {
        log.info("Checking fraud against {} rules", fraudRuleMap.size());

        try {
            // fraud check in parallel between available rules (components in fraudRuleMap)
            List<FraudRuleScore> fraudScoreList = checkFraudParallel(transInfo);

            String messages = getMessage(fraudScoreList);
            Integer fraudScore = getScore(fraudScoreList);

            if (fraudScore > 0) {
                log.info("Total fraud score calculated::{}", fraudScore);
                return new FraudCheckResponse(FraudCheckResponse.RejStatus.DENIED, messages, fraudScore > 100 ? 100 : fraudScore);
            } else {
                return new FraudCheckResponse(FraudCheckResponse.RejStatus.ALLOWED, null, 0);
            }

        } catch (Exception e) {
            log.error("An unexpected error occurred during fraud score calculation!", e);
            throw new FraudCheckException("An unexpected error occurred during fraud score calculation", e);
        }
    }

    public FraudCheckResponse handleInvalidTerminal() {
        return new FraudCheckResponse(FraudCheckResponse.RejStatus.DENIED, "Invalid terminal", 100);
    }

    private List<FraudRuleScore> checkFraudParallel(IncomingTransactionInfo transInfo) {
        List<FraudRuleScore> fraudScoreList = new ArrayList<>();
        fraudRuleMap.entrySet().parallelStream()
                .forEach(rule -> fraudScoreList.add(fraudRuleMap.get(rule.getKey()).checkFraud(transInfo)));

        return fraudScoreList;
    }

    private String getMessage(List<FraudRuleScore> fraudScoreList) {
        return fraudScoreList.stream()
                .map(FraudRuleScore::getMessage)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(";"));
    }

    private Integer getScore(List<FraudRuleScore> fraudScoreList) {
        return fraudScoreList.stream()
                .mapToInt(FraudRuleScore::getScore).sum();
    }
}
