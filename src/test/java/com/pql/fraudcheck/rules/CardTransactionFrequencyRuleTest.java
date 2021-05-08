package com.pql.fraudcheck.rules;

import com.pql.fraudcheck.dto.FraudRuleScore;
import com.pql.fraudcheck.dto.IncomingTransactionInfo;
import com.pql.fraudcheck.exception.CorruptedDataException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class CardTransactionFrequencyRuleTest {

    IFraudDetection cardTransactionFrequencyRule;


    @Before
    public void setUp() {
        cardTransactionFrequencyRule = new CardTransactionFrequencyRule();
    }

    @Test
    public void testCheckFraudLowTrans() throws Exception {
        int transNum = 5;

        FraudRuleScore response = getFraudScore(transNum);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());
    }

    @Test
    public void testCheckFraud30Trans() throws Exception {
        int transNum = 30;

        FraudRuleScore response = getFraudScore(transNum);

        assertEquals(15, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraud70Trans() throws Exception {
        int transNum = 70;

        FraudRuleScore response = getFraudScore(transNum);

        assertEquals(50, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraud120Trans() throws Exception {
        int transNum = 120;

        FraudRuleScore response = getFraudScore(transNum);

        assertEquals(80, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test(expected = CorruptedDataException.class)
    public void testCheckFraudScoreNegativeTransNumber() throws Exception {
        getFraudScore(-5);
    }

    private FraudRuleScore getFraudScore(int transNum) {
        IncomingTransactionInfo transInfo = new IncomingTransactionInfo(400.00, "GBP", 20, transNum, 1.234, 1.234, 78, 1.234, 1.234);
        return cardTransactionFrequencyRule.checkFraud(transInfo);
    }
}
