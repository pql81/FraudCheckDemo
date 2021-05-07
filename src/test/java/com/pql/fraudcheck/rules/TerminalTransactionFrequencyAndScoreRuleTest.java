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
public class TerminalTransactionFrequencyAndScoreRuleTest {

    IFraudDetection terminalTransactionFrequencyAndScoreRule;


    @Before
    public void setUp() {
        terminalTransactionFrequencyAndScoreRule = new TerminalTransactionFrequencyAndScoreRule();
    }

    @Test
    public void testCheckFraudLowThreatScore() throws Exception {
        int threatScore = 10;
        int transNum = 175;

        FraudRuleScore response = getFraudScore(threatScore, transNum);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());
    }

    @Test
    public void testCheckFraudLowTrans() throws Exception {
        int threatScore = 20;
        int transNum = 275;

        FraudRuleScore response = getFraudScore(threatScore, transNum);

        assertEquals(0, response.getScore().intValue());
        assertNull(response.getMessage());
    }

    @Test
    public void testCheckFraudScore15() throws Exception {
        int threatScore = 35;
        int transNum = 275;

        FraudRuleScore response = getFraudScore(threatScore, transNum);

        assertEquals(15, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraudScore30() throws Exception {
        int threatScore = 35;
        int transNum = 435;

        FraudRuleScore response = getFraudScore(threatScore, transNum);

        assertEquals(30, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraudScore45() throws Exception {
        int threatScore = 60;
        int transNum = 120;

        FraudRuleScore response = getFraudScore(threatScore, transNum);

        assertEquals(45, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testCheckFraudScore60() throws Exception {
        int threatScore = 60;
        int transNum = 170;

        FraudRuleScore response = getFraudScore(threatScore, transNum);

        assertEquals(60, response.getScore().intValue());
        assertNotNull(response.getMessage());
    }

    @Test(expected = CorruptedDataException.class)
    public void testCheckFraudScoreNegativeThreatScore() throws Exception {
        getFraudScore(-5, 200);
    }

    @Test(expected = CorruptedDataException.class)
    public void testCheckFraudScoreNegativeTransNumber() throws Exception {
        getFraudScore(15, -45);
    }

    private FraudRuleScore getFraudScore(int threatScore, int transNum) {
        IncomingTransactionInfo transInfo = new IncomingTransactionInfo(400.00, "GBP", threatScore, 15, 1.234, 1.234, transNum, 1.234, 1.234);
        return terminalTransactionFrequencyAndScoreRule.checkFraud(transInfo);
    }
}
