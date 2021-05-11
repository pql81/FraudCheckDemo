package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CorruptedDataException;
import com.pql.fraudcheck.exception.CurrencyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Created by pasqualericupero on 11/05/2021.
 */
@Log4j2
@Component("CURRENCY_BLACKLIST")
public class CurrencyBlackListRule implements IFraudDetection {

    @Value("${fraud.rule.currency.enabled:true}")
    private boolean enabled;

    private final List<String> currencyBlacklist;


    public CurrencyBlackListRule(@Value("${fraud.rule.currency.blacklist:}") List<String> currencyBlacklist) {
        if (currencyBlacklist == null || currencyBlacklist.isEmpty()) {
            this.currencyBlacklist = Collections.emptyList();
        } else {
            this.currencyBlacklist = currencyBlacklist;
        }
    }

    @Override
    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) throws CurrencyException, CorruptedDataException {
        int fraudScore = 0;
        String message = null;

        // no need for currency check so far - it happens as part of the request validation in FraudCheckService
        if (currencyBlacklist != null && currencyBlacklist.contains(transInfo.getCurrency())) {
            fraudScore = 75;
            message = "Transaction currency not allowed";
            log.warn("{}::{}", message, transInfo.getCurrency());
        }

        return new FraudRuleScore(fraudScore, message);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
