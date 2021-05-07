package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.util.CurrencyThreshold;
import org.springframework.stereotype.Component;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Component
public class FraudRulesHandler {

    public FraudCheckResponse checkIncomingTransaction(IncomingTransactionInfo transInfo) {
        Integer fraudScore = 0;
        if (transInfo.getThreatScore() > 0 && transInfo.getAmount() > CurrencyThreshold.valueOf(transInfo.getCurrency()).threshold / transInfo.getThreatScore()) {
            fraudScore+=transInfo.getThreatScore()/5;
        }
        if (fraudScore > 0) {
            return new FraudCheckResponse(FraudCheckResponse.RejStatus.DENIED, "Amount too large", fraudScore);
        } else {
            return new FraudCheckResponse(FraudCheckResponse.RejStatus.ALLOWED, null, 0);
        }
    }

    public FraudCheckResponse handleInvalidTerminal(Integer threatScore) {
        return new FraudCheckResponse(FraudCheckResponse.RejStatus.DENIED, "Invalid terminal", 100);
    }
}
