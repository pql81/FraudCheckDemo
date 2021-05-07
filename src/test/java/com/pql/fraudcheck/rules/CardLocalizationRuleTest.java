package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by pasqualericupero on 08/05/2021.
 */
public class CardLocalizationRuleTest {

    IFraudDetection cardLocalizationRule;


    @Before
    public void setUp() {
        cardLocalizationRule = new CardLocalizationRule();
    }

    @Test
    public void testCheckFraudSuccess() throws Exception {
        FraudRuleScore response = getFraudScore(-1.123, 2.567, -1.115, 2.575);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());
    }

    @Test
    public void testCheckFraudFailure() throws Exception {
        FraudRuleScore response = getFraudScore(-1.123, 2.567, -1.423, 2.067);

        assertEquals(25, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    private FraudRuleScore getFraudScore(double cardLat, double cardLong, double terminalLat, double terminalLong) {
        IncomingTransactionInfo transInfo = new IncomingTransactionInfo(200.00, "EUR", 0, 19, cardLat, cardLong, 78, terminalLat, terminalLong);
        return cardLocalizationRule.checkFraud(transInfo);
    }
}
