package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CorruptedDataException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Component("CARD_FREQUENCY")
public class CardTransactionFrequencyRule implements IFraudDetection {

    @Override
    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) {
        if (transInfo.getRecentCardTransactionNumber() < 0) {
            // it shouldn't happen as input data is validated at controller level
            log.warn("Data corrupted during fraud check process");
            throw new CorruptedDataException("Corrupted data in input");
        }

        Integer transFrequency = transInfo.getRecentCardTransactionNumber();
        log.info("Processing card recent transaction number::{}", transFrequency);

        Integer fraudScore;
        String message = null;

        if (transFrequency <= 25) {
            fraudScore = 0;
        } else if (transFrequency <= 50) {
            fraudScore = 15;
        } else if (transFrequency <= 100) {
            fraudScore = 50;
        } else {
            fraudScore = 80;
        }

        if (fraudScore > 0) {
            message = "Card transaction frequency suspicious";
            log.warn("{}::{}", message, transFrequency);
        }

        return new FraudRuleScore(fraudScore, message);
    }
}
