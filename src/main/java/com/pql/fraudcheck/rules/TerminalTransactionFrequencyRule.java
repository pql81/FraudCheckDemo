package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@Log4j2
@Component("TRANS_FREQUENCY")
public class TerminalTransactionFrequencyRule implements IFraudDetection {

    @Override
    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) {
        Integer transFrequency = transInfo.getRecentTerminalTransactionNumber();
        log.info("Processing terminal recent transaction number::{}", transFrequency);

        Integer fraudScore;
        String message = null;

        if (transFrequency <= 250) {
            fraudScore = 0;
        } else if (transFrequency <= 750) {
            fraudScore = 30;
        } else {
            fraudScore = 70;
        }

        if (fraudScore > 0) {
            message = "Terminal transaction frequency suspicious";
            log.warn("{}::{}", message, transFrequency);
        }

        return new FraudRuleScore(fraudScore, message);
    }
}
