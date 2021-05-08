package com.pql.fraudcheck;

import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.repository.FraudDetectedRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.nullValue;

/**
 * Created by pasqualericupero on 08/05/2021.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FraudCheckDemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FraudDetectedRepository raudDetectedRepository;


    @Test
    public void testFraudCheckAllowed() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"GBP\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"5555444455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.ALLOWED.name()))
                .andExpect(jsonPath("$.rejectionMessage").value(nullValue()))
                .andExpect(jsonPath("$.fraudScore").value(0));
    }

    @Test
    public void testFraudCheckDeniedAmount() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":1000,\"currency\":\"GBP\",\"terminalId\":\"T0100\",\"threatScore\":20,\"cardNumber\":\"5555444455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.DENIED.name()))
                .andExpect(jsonPath("$.rejectionMessage").value("Transaction amount exceeds the upper limit for the terminal"))
                .andExpect(jsonPath("$.fraudScore").value(25));
    }

    @Test
    public void testFraudCheckDeniedTerminal() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"GBP\",\"terminalId\":\"T0102\",\"threatScore\":10,\"cardNumber\":\"5555444455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.DENIED.name()))
                .andExpect(jsonPath("$.rejectionMessage").value("Terminal transaction frequency suspicious"))
                .andExpect(jsonPath("$.fraudScore").value(10));
    }

    @Test
    public void testFraudCheckDeniedCard() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"GBP\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"5555444455554431\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.DENIED.name()))
                .andExpect(jsonPath("$.rejectionMessage").value("Card transaction frequency suspicious"))
                .andExpect(jsonPath("$.fraudScore").value(15));
    }

    @Test
    public void testFraudCheckDeniedCurrency() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"ABC\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"5555444455554431\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFraudCheckDeniedCurrencyFormat() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"GB\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"5555444455554431\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFraudCheckDeniedCombined() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":2000,\"currency\":\"GBP\",\"terminalId\":\"T0102\",\"threatScore\":40,\"cardNumber\":\"5555444455554431\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.DENIED.name()))
                .andExpect(jsonPath("$.fraudScore").value(80))
                .andExpect(jsonPath("$.rejectionMessage").isNotEmpty());
    }
}
