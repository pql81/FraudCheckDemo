package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CorruptedDataException;
import com.pql.fraudcheck.exception.CurrencyException;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
public interface IFraudDetection {

    FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) throws CurrencyException, CorruptedDataException;

    boolean isEnabled();
}
