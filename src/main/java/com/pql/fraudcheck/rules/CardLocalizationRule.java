package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Component("CARD_LOCATION")
public class CardLocalizationRule implements IFraudDetection {

    @Override
    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) {
        log.info("Processing card recent location against terminal");

        Integer fraudScore = 0;
        String message = null;

        // mocked service - just place some trivial logic to eventually return a value different from 0
        if (Math.abs(transInfo.getTerminalLat() - transInfo.getCardLastLocationLat()) > 10
            || Math.abs(transInfo.getTerminalLong() - transInfo.getCardLastLocationLong()) > 10) {
            fraudScore = 40;
        }

        if (fraudScore > 0) {
            message =  "Location of last card transaction is suspicious";
            log.warn("{}::{},{}", message, transInfo.getCardLastLocationLat(), transInfo.getCardLastLocationLong());
        }

        return new FraudRuleScore(fraudScore, message);
    }
}
