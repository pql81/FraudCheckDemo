package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CorruptedDataException;
import com.pql.fraudcheck.exception.CurrencyException;
import lombok.extern.log4j.Log4j2;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.format.CurrencyStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.MonetaryConversions;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Locale;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Component("AMOUNT_SCORE")
public class AmountAndScoreRule implements IFraudDetection {

    @Value("${fraud.check.rule.amount.score.enabled:true}")
    private boolean enabled;

    private final CurrencyConversion conversionForCalculation;
    private final FastMoney amountThreshold;
    private final MonetaryAmountFormat format;


    public AmountAndScoreRule(@Value("${fraud.amount.threshold.value}") int threshold,
                              @Value("${fraud.amount.threshold.currency}") String thresholdCurrency) {
        this.conversionForCalculation = MonetaryConversions.getConversion(thresholdCurrency);
        this.amountThreshold = FastMoney.of(threshold, thresholdCurrency);

        this.format = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder.of(Locale.ITALY) // it's just the way to display amount and currency
                        .set(CurrencyStyle.CODE)
                        .build()
        );
    }

    @Override
    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) {
        checkForInvalidInput(transInfo);

        try {
            CurrencyUnit inputCurrency = Monetary.getCurrency(transInfo.getCurrency());
            FastMoney amount = FastMoney.of(transInfo.getAmount(), inputCurrency);

            log.info("Processing input amount::{}", format.format(amount));

            FastMoney inputAmountNormalized = amount.with(conversionForCalculation);
            FastMoney applicableThreshold = calculateApplicableThreshold(transInfo.getThreatScore());

            log.info("Calculated threshold::{}", format.format(applicableThreshold));

            Integer fraudScore = 0;
            String message = null;
            if (inputAmountNormalized.isGreaterThan(applicableThreshold)) {
                message = "Transaction amount exceeds the upper limit for the terminal";
                fraudScore = calculateFraudScore(transInfo.getThreatScore());
                log.warn("{}::{}", message, format.format(inputAmountNormalized));
            }

            return new FraudRuleScore(fraudScore, message);

        } catch (MonetaryException ce) {
            if (ce.getCause() instanceof CurrencyConversionException) {
                log.error("Cannot convert currency [{}] for calculation", transInfo.getCurrency());
                throw new CurrencyException("Cannot convert currency for calculation", ce);
            } else {
                log.error("Could not check request amount against threshold");
                throw new CurrencyException("Could not check request amount against threshold", ce);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
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

    private Integer calculateFraudScore(Integer threatScore) {
        return threatScore > 50 ? 50 : 25;
    }

    private void checkForInvalidInput(IncomingTransactionInfo info) {
        if (info.getThreatScore() < 0 || info.getAmount() < 0) {
            // it shouldn't happen as input data is validated at controller level
            log.warn("Data corrupted during fraud check process");
            throw new CorruptedDataException("Corrupted data in input");
        }
    }
}
