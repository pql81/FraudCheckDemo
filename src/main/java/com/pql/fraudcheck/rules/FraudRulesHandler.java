package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CurrencyException;
import com.pql.fraudcheck.exception.FraudCheckException;
import com.pql.fraudcheck.util.MDCStreamHelper;
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

    private final List<IFraudDetection> applicableFraudRuleList;

    private final static Integer MIN_FRAUD_SCORE = 0;
    private final static Integer MAX_FRAUD_SCORE = 100;


    public FraudRulesHandler(Map<String, IFraudDetection> fraudRuleMap) {
        // collect only enabled rules to List
        this.applicableFraudRuleList = fraudRuleMap.entrySet().stream()
                .filter(map -> map.getValue().isEnabled())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public FraudCheckResponse checkIncomingTransaction(IncomingTransactionInfo transInfo) {
        int rulesNum = applicableFraudRuleList.size();
        log.info("Checking fraud against {} rules", rulesNum);

        // not really a good scenario - log an alert
        if (rulesNum == 0) {
            log.warn("RUNNING FRAUD CHECK WITH NO RULE ENABLED! PLEASE CHECK CONFIGURATION");
        }

        try {
            // fraud check in parallel between available rules (components in fraudRuleMap)
            List<FraudRuleScore> fraudScoreList = checkFraudParallel(transInfo);

            String messages = getMessage(fraudScoreList);
            Integer fraudScore = getScore(fraudScoreList);

            if (fraudScore > 0) {
                fraudScore = fraudScore > MAX_FRAUD_SCORE ? MAX_FRAUD_SCORE : fraudScore; // fraud score cannot exceed 100
                log.info("Total fraud score calculated::{}", fraudScore);
                return new FraudCheckResponse(FraudCheckResponse.RejStatus.REJECTED, messages, fraudScore);
            } else {
                return new FraudCheckResponse(FraudCheckResponse.RejStatus.ALLOWED, null, MIN_FRAUD_SCORE);
            }

        } catch (CurrencyException ce) {
            throw ce;
        } catch (Exception e) {
            log.error("An unexpected error occurred during fraud score calculation!", e);
            throw new FraudCheckException("An unexpected error occurred during fraud score calculation", e);
        }
    }

    public FraudCheckResponse handleInvalidTerminal() {
        return new FraudCheckResponse(FraudCheckResponse.RejStatus.REJECTED, "Invalid terminal", MAX_FRAUD_SCORE);
    }

    private List<FraudRuleScore> checkFraudParallel(IncomingTransactionInfo transInfo) {
        List<FraudRuleScore> fraudScoreList = new ArrayList<>();
        MDCStreamHelper mdc = MDCStreamHelper.getCurrentMdc();
        applicableFraudRuleList.parallelStream()
                .forEach(rule -> {
                        mdc.setMdc();
                        fraudScoreList.add(rule.checkFraud(transInfo));
                });

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
