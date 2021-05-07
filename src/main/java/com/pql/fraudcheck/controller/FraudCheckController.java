package com.pql.fraudcheck.controller;

import com.pql.fraudcheck.dto.FraudCheckRequest;
import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.service.TransFraudService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pasqualericupero on 05/05/2021.
 */
@RestController
@Log4j2
public class FraudCheckController {

    @Autowired
    private TransFraudService transFraudService;


    @PostMapping("/fraud-check")
    public ResponseEntity<FraudCheckResponse> fraudCheck(@Validated @RequestBody FraudCheckRequest request) {
        log.info("POST fraud-check " + request);

        FraudCheckResponse response = transFraudService.checkAllFraudRules(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
