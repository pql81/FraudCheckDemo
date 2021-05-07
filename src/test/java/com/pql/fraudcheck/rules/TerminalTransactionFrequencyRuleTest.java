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
public class TerminalTransactionFrequencyRuleTest {

    IFraudDetection terminalTransactionFrequencyRule;


    @Before
    public void setUp() {
        terminalTransactionFrequencyRule = new TerminalTransactionFrequencyRule();
    }

    @Test
    public void testCheckFraudLowTrans() throws Exception {
        int transNum = 175;

        FraudRuleScore response = getFraudScore(transNum);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());
    }

    @Test
    public void testCheckFraudMoreThan600Trans() throws Exception {
        int transNum = 640;

        FraudRuleScore response = getFraudScore(transNum);

        assertEquals(10, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraudMoreThan1000Trans() throws Exception {
        int transNum = 1200;

        FraudRuleScore response = getFraudScore(transNum);

        assertEquals(30, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraudMoreThan2000Trans() throws Exception {
        int transNum = 2500;

        FraudRuleScore response = getFraudScore(transNum);

        assertEquals(50, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test(expected = CorruptedDataException.class)
    public void testCheckFraudScoreNegativeTransNumber() throws Exception {
        getFraudScore(-5);
    }

    private FraudRuleScore getFraudScore(int transNum) {
        IncomingTransactionInfo transInfo = new IncomingTransactionInfo(400.00, "GBP", 20, 15, 1.234, 1.234, transNum, 1.234, 1.234);
        return terminalTransactionFrequencyRule.checkFraud(transInfo);
    }
}
