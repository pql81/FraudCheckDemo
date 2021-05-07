package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
public interface IFraudDetection {

    FraudRuleScore checkFraud(IncomingTransactionInfo transInfo);
}
