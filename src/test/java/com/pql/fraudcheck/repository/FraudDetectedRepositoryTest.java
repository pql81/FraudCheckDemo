package com.pql.fraudcheck.repository;

import com.pql.fraudcheck.domain.FraudDetected;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class FraudDetectedRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    FraudDetectedRepository fraudDetectedRepository;


    @Test
    public void testSaveAndFindDetectedFraud() {
        FraudDetected fraud = new FraudDetected();
        fraud.setAmount(10.5);
        fraud.setCurrency("EUR");
        fraud.setLastCardDigits("0000");
        fraud.setThreatScore(20);
        fraud.setTerminalId("T001");
        fraud.setRejectionMessage("test");
        fraud.setFraudScore(15);
        fraud = entityManager.persistAndFlush(fraud);

        assertEquals(fraud, fraudDetectedRepository.findById(fraud.getId()).get());
    }
}
