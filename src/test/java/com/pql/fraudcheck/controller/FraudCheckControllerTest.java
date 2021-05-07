package com.pql.fraudcheck.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.assertEquals;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FraudCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testPostOk() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"EUR\",\"terminalId\":\"T0102\",\"threatScore\":40,\"cardNumber\":\"5555444455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostBadInvalidCard() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"EUR\",\"terminalId\":\"T0102\",\"threatScore\":40,\"cardNumber\":\"5555455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("cardNumber"));
    }

    @Test
    public void testPostInvalidCurrency() throws Exception {

        MvcResult resp = mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"NNN\",\"terminalId\":\"T0102\",\"threatScore\":40,\"cardNumber\":\"5555444455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String error = resp.getResolvedException().getMessage();
        assertEquals("Invalid currency ISO code", error);
    }

    @Test
    public void testPostInvalidCurrencyISOCode() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"GB\",\"terminalId\":\"T0102\",\"threatScore\":40,\"cardNumber\":\"5555444455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("currency"));
    }

    @Test
    public void testPostInvalidThreatScore() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"EUR\",\"terminalId\":\"T0102\",\"threatScore\":140,\"cardNumber\":\"5555444455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("threatScore"));
    }

    @Test
    public void testPostEmptyTerminalId() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":600,\"currency\":\"EUR\",\"terminalId\":\" \",\"threatScore\":40,\"cardNumber\":\"5555444455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("terminalId"));
    }

    @Test
    public void testPostNegativeAmount() throws Exception {

        mockMvc.perform(post("/fraud-check")
                .content("{\"amount\":-200,\"currency\":\"EUR\",\"terminalId\":\"T0102\",\"threatScore\":40,\"cardNumber\":\"5555444455554433\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("amount"));
    }

    @Test
    public void testGetNotAllowed() throws Exception {

        mockMvc.perform(get("/fraud-check")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
}
