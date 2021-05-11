package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by pasqualericupero on 11/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class CurrencyBlackListRuleTest {

    IFraudDetection cardLocalizationRule;


    @Before
    public void setUp() {
        cardLocalizationRule = new CurrencyBlackListRule(Arrays.asList("GBP", "AUD"));
    }

    @Test
    public void testCheckFraudCurrencyOK() throws Exception {
        FraudRuleScore response = getFraudScore("EUR");

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());
    }

    @Test
    public void testCheckFraudCurrencyBlacklisted() throws Exception {
        FraudRuleScore response = getFraudScore("AUD");

        assertEquals(75, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    private FraudRuleScore getFraudScore(String currency) {
        IncomingTransactionInfo transInfo = new IncomingTransactionInfo(200.00, currency, 15, 19, 1.234, 1.234, 78, 1.234, 1.234);
        return cardLocalizationRule.checkFraud(transInfo);
    }
}
