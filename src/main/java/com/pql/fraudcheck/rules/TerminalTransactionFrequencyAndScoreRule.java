package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CorruptedDataException;
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
        if (transInfo.getThreatScore() < 0 || transInfo.getRecentTerminalTransactionNumber() < 0) {
            // it shouldn't happen as input data is validated at controller level
            log.warn("Data corrupted during fraud check process");
            throw new CorruptedDataException("Corrupted data in input");
        }

        Integer transFrequency = transInfo.getRecentTerminalTransactionNumber();
        Integer threatScore = transInfo.getThreatScore();
        log.info("Processing terminal recent transaction number against its threat score::{}", threatScore);

        Integer fraudScore = 0;
        String message = null;

        if (threatScore < 15 && transFrequency < 200) {
            fraudScore = 0;
        } else if (threatScore < 25 && transFrequency < 300) {
            fraudScore = 0;
        } else if (threatScore < 50 && transFrequency < 300) {
            fraudScore = 15;
        } else if (threatScore < 50 && transFrequency >= 300) {
            fraudScore = 30;
        } else if (threatScore >= 50 && transFrequency < 150) {
            fraudScore = 45;
        } else {
            fraudScore = 60;
        }

        if (fraudScore > 0) {
            message = "Terminal transaction frequency suspicious according to its threat score";
            log.warn("{}::{} score::{}", message, transFrequency, threatScore);
        }

        return new FraudRuleScore(fraudScore, message);
    }
}
