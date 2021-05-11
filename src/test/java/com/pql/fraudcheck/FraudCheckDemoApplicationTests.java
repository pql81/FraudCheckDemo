package com.pql.fraudcheck;

import com.pql.fraudcheck.dto.FraudCheckResponse;
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


    @Test
    public void testFraudCheckAllowed() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"GBP\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"gbgkB1su1FwtCityUQo6ofwMMsMik9/jvjcIwWIobCE=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.ALLOWED.name()))
                .andExpect(jsonPath("$.rejectionMessage").value(nullValue()))
                .andExpect(jsonPath("$.fraudScore").value(0));
    }

    @Test
    public void testFraudCheckDeniedAmount() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":1000,\"currency\":\"GBP\",\"terminalId\":\"T0100\",\"threatScore\":20,\"cardNumber\":\"gbgkB1su1FwtCityUQo6ofwMMsMik9/jvjcIwWIobCE=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.DENIED.name()))
                .andExpect(jsonPath("$.rejectionMessage").value("Transaction amount exceeds the upper limit for the terminal"))
                .andExpect(jsonPath("$.fraudScore").value(25));
    }

    @Test
    public void testFraudCheckDeniedTerminal() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"GBP\",\"terminalId\":\"T0102\",\"threatScore\":10,\"cardNumber\":\"gbgkB1su1FwtCityUQo6ofwMMsMik9/jvjcIwWIobCE=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.DENIED.name()))
                .andExpect(jsonPath("$.rejectionMessage").value("Terminal transaction frequency suspicious"))
                .andExpect(jsonPath("$.fraudScore").value(10));
    }

    @Test
    public void testFraudCheckDeniedCard() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"GBP\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"TrqkmOR3U/xi1Ks8I+hLp2dt4DCei7Uzoi/bU4cH2Ck=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.DENIED.name()))
                .andExpect(jsonPath("$.rejectionMessage").value("Card transaction frequency suspicious"))
                .andExpect(jsonPath("$.fraudScore").value(15));
    }

    @Test
    public void testFraudCheckDeniedCardNumberTooShort() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"EUR\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"tEE/B1pfMoDCJ4rs+Kj1ew==\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFraudCheckDeniedCardNumberTooLong() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"USD\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"yn+alOw6qL669H09HQEMEcXAY0P4B4o92sbNbeDByOg=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFraudCheckDeniedCurrency() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"ABC\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"TrqkmOR3U/xi1Ks8I+hLp2dt4DCei7Uzoi/bU4cH2Ck=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testFraudCheckDeniedObsoleteCurrency() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"ITL\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"TrqkmOR3U/xi1Ks8I+hLp2dt4DCei7Uzoi/bU4cH2Ck=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testFraudCheckDeniedBlacklistedCurrency() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"AUD\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"gbgkB1su1FwtCityUQo6ofwMMsMik9/jvjcIwWIobCE=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.DENIED.name()))
                .andExpect(jsonPath("$.rejectionMessage").value("Transaction currency not allowed"))
                .andExpect(jsonPath("$.fraudScore").value(75));
    }

    @Test
    public void testFraudCheckDeniedCurrencyFormat() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"GB\",\"terminalId\":\"T0100\",\"threatScore\":10,\"cardNumber\":\"TrqkmOR3U/xi1Ks8I+hLp2dt4DCei7Uzoi/bU4cH2Ck=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFraudCheckDeniedCombined() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":2000,\"currency\":\"GBP\",\"terminalId\":\"T0102\",\"threatScore\":40,\"cardNumber\":\"TrqkmOR3U/xi1Ks8I+hLp2dt4DCei7Uzoi/bU4cH2Ck=\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectionStatus").value(FraudCheckResponse.RejStatus.DENIED.name()))
                .andExpect(jsonPath("$.fraudScore").value(80))
                .andExpect(jsonPath("$.rejectionMessage").isNotEmpty());
    }
}
