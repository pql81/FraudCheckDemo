package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CorruptedDataException;
import com.pql.fraudcheck.exception.CurrencyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@Log4j2
@Component("TRANS_FREQUENCY")
public class TerminalTransactionFrequencyRule implements IFraudDetection {

    @Value("${fraud.rule.terminal.transactions.enabled:true}")
    private boolean enabled;


    @Override
    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) throws CurrencyException, CorruptedDataException {
        if (transInfo.getRecentTerminalTransactionNumber() < 0) {
            // it shouldn't happen as input data is validated at controller level
            log.error("Data corrupted during fraud check process");
            throw new CorruptedDataException("Corrupted data in input");
        }

        Integer transFrequency = transInfo.getRecentTerminalTransactionNumber();
        log.info("Processing terminal recent transaction number::{}", transFrequency);

        Integer fraudScore;
        String message = null;

        if (transFrequency <= 250) {
            fraudScore = 0;
        } else if (transFrequency <= 750) {
            fraudScore = 10;
        } else if (transFrequency <= 2000) {
            fraudScore = 30;
        } else {
            fraudScore = 50;
        }

        if (fraudScore > 0) {
            message = "Terminal transaction frequency suspicious";
            log.warn("{}::{}", message, transFrequency);
        }

        return new FraudRuleScore(fraudScore, message);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
