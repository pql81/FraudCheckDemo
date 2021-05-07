package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@Log4j2
@Component("TRANS_FREQUENCY_SCORE")
public class TerminalTransactionFrequencyAndScoreRule implements IFraudDetection {

    @Override
    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) {
        Integer transFrequency = transInfo.getRecentTerminalTransactionNumber();
        Integer threatScore = transInfo.getThreatScore();
        log.info("Processing terminal recent transaction number against its threat score::{}", threatScore);

        Integer fraudScore = 0;
        String message = null;

        if (threatScore > 25 && transFrequency > 200) {
            fraudScore = 15;
        } else if (threatScore > 50 && transFrequency > 100) {
            fraudScore = 30;
        } else if (threatScore > 70 && transFrequency > 50) {
            fraudScore = 45;
        }

        if (fraudScore > 0) {
            message = "Terminal transaction frequency suspicious according to its threat score";
            log.warn("{}::{} score::{}", message, transFrequency, threatScore);
        }

        return new FraudRuleScore(fraudScore, message);
    }
}
