package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import lombok.extern.log4j.Log4j2;
import org.javamoney.moneta.FastMoney;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Component("AMOUNT_SCORE")
public class AmountAndScoreRule implements IFraudDetection {

    @Value("${fraud.amount.threshold.value}")
    private int threshold;
    @Value("${fraud.amount.threshold.currency}")
    private String thresholdCurrency;

    private CurrencyConversion conversionForCalculation;
    private FastMoney amountThreshold;


    @PostConstruct
    private void initCurrencyConverter() {
        this.conversionForCalculation = MonetaryConversions.getConversion(thresholdCurrency);
        this.amountThreshold = FastMoney.of(threshold, thresholdCurrency);
    }

    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) {
        CurrencyUnit inputCurrency = Monetary.getCurrency(transInfo.getCurrency());
        FastMoney amount = FastMoney.of(transInfo.getAmount(), inputCurrency);

        log.info("Processing input amount::{}", amount.toString());

        FastMoney inputAmountNormalized = amount.with(conversionForCalculation);
        FastMoney applicableThreshold = calculateApplicableThreshold(transInfo.getThreatScore());

        log.info("Calculated threshold::{}", applicableThreshold.toString());

        Integer fraudScore = 0;
        String message = null;
        if (inputAmountNormalized.isGreaterThan(applicableThreshold)) {
            message = "Transaction amount exceeds the upper limit for the terminal";
            fraudScore=25;
            log.warn("{}::{}", message, inputAmountNormalized.toString());
        }

        return new FraudRuleScore(fraudScore, message);
    }

    private FastMoney calculateApplicableThreshold(Integer threatScore) {
        FastMoney applicableThreshold;

        if (threatScore < 15) {
            applicableThreshold = amountThreshold.multiply(1);
        } else if (threatScore < 30) {
            applicableThreshold = amountThreshold.multiply(0.75);
        } else if (threatScore < 50) {
            applicableThreshold = amountThreshold.multiply(0.2);
        } else if (threatScore < 80) {
            applicableThreshold = amountThreshold.divide(20);
        } else {
            applicableThreshold = amountThreshold.divide(50);
        }

        return applicableThreshold;
    }
}
