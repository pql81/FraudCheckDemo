package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CorruptedDataException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class AmountAndScoreRuleTest {

    IFraudDetection amountAndScoreRule;


    @Before
    public void setUp() {
        amountAndScoreRule = new AmountAndScoreRule(500, "EUR");
    }

    @Test
    public void testAmountAndScoreRuleBadThresholdAmount() throws Exception {
        AmountAndScoreRule rule = new AmountAndScoreRule(null, "USD");
        assertNotNull(rule.checkFraud(getIncomingTransactionInfoForTest(250.50, 30)));

        rule = new AmountAndScoreRule(-120, "USD");
        assertNotNull(rule.checkFraud(getIncomingTransactionInfoForTest(250.50, 30)));
    }

    @Test
    public void testAmountAndScoreRuleBadThresholdCurrency() throws Exception {
        AmountAndScoreRule rule = new AmountAndScoreRule(500, "");
        assertNotNull(rule.checkFraud(getIncomingTransactionInfoForTest(250.50, 30)));

        rule = new AmountAndScoreRule(500, "ABC");
        assertNotNull(rule.checkFraud(getIncomingTransactionInfoForTest(250.50, 30)));
    }

    @Test
    public void testCheckFraudScore5() throws Exception {
        int threatScore = 5;

        FraudRuleScore response = getFraudScore(470.50, threatScore);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());

        response = getFraudScore(540.50, threatScore);

        assertEquals(25, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraudScore25() throws Exception {
        int threatScore = 25;

        FraudRuleScore response = getFraudScore(370.50, threatScore);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());

        response = getFraudScore(390.50, threatScore);

        assertEquals(25, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraudScore40() throws Exception {
        int threatScore = 40;

        FraudRuleScore response = getFraudScore(94.50, threatScore);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());

        response = getFraudScore(102.50, threatScore);

        assertEquals(25, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraudScore60() throws Exception {
        int threatScore = 60;

        FraudRuleScore response = getFraudScore(22.50, threatScore);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());

        response = getFraudScore(26.50, threatScore);

        assertEquals(50, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraudScore90() throws Exception {
        int threatScore = 90;

        FraudRuleScore response = getFraudScore(9.50, threatScore);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());

        response = getFraudScore(12.50, threatScore);

        assertEquals(50, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test(expected = CorruptedDataException.class)
    public void testCheckFraudScoreNegativeAmount() throws Exception {
        getFraudScore(-9.50, 10);
    }

    @Test(expected = CorruptedDataException.class)
    public void testCheckFraudScoreNegativeThreatScore() throws Exception {
        getFraudScore(9.50, -5);
    }

    private FraudRuleScore getFraudScore(double amount, int threatScore) {
        return amountAndScoreRule.checkFraud(getIncomingTransactionInfoForTest(amount, threatScore));
    }

    private IncomingTransactionInfo getIncomingTransactionInfoForTest(double amount, int threatScore) {
        return new IncomingTransactionInfo(amount, "EUR", threatScore, 19, 1.234, 1.234, 78, 1.234, 1.234);
    }
}
