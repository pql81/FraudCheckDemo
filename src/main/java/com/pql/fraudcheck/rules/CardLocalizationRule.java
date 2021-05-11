package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CorruptedDataException;
import com.pql.fraudcheck.exception.CurrencyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Component("CARD_LOCATION")
public class CardLocalizationRule implements IFraudDetection {

    @Value("${fraud.check.rule.card.localization.enabled:true}")
    private boolean enabled;


    @Override
    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) throws CurrencyException, CorruptedDataException {
        log.info("Processing card recent location against terminal");

        Integer fraudScore = 0;
        String message = null;

        if (transInfo.getCardLastLocationLat() != null && transInfo.getCardLastLocationLong() != null) {
            // mocked service - just place some trivial logic to eventually return a value different from 0
            if (Math.abs(transInfo.getTerminalLat() - transInfo.getCardLastLocationLat()) > 0.01
                    || Math.abs(transInfo.getTerminalLong() - transInfo.getCardLastLocationLong()) > 0.01) {
                fraudScore = 25;
            }

            if (fraudScore > 0) {
                message = "Location of last card transaction is suspicious";
                log.warn("{}::{},{}", message, transInfo.getCardLastLocationLat(), transInfo.getCardLastLocationLong());
            }
        } else {
            log.info("Card last location not available - skip");
        }

        return new FraudRuleScore(fraudScore, message);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
