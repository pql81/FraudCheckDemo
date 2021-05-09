package com.pql.fraudcheck.controller;

import com.pql.fraudcheck.domain.FraudDetected;
import com.pql.fraudcheck.dto.FraudCheckRequest;
import com.pql.fraudcheck.dto.FraudCheckResponse;
import com.pql.fraudcheck.exception.CardPanException;
import com.pql.fraudcheck.service.FraudDetectedService;
import com.pql.fraudcheck.service.SimpleEncryptionService;
import com.pql.fraudcheck.service.TransFraudService;
import com.pql.fraudcheck.util.LogHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Created by pasqualericupero on 05/05/2021.
 */
@Log4j2
@RestController
public class FraudCheckController {

    @Autowired
    private TransFraudService transFraudService;

    @Autowired
    private FraudDetectedService fraudDetectedService;

    @Autowired
    private SimpleEncryptionService encryptionService;


    @PostMapping("/fraud-check")
    public ResponseEntity<FraudCheckResponse> fraudCheck(@Validated @RequestBody FraudCheckRequest request) {
        log.info("POST fraud-check " + request);

        boolean success = true;
        String errorMsg = null;

        try {
            String decryptedPan = encryptionService.decrypt(request.getCardNumber());
            if (decryptedPan.length() < 13 || decryptedPan.length() > 16) {
                throw new CardPanException("Card pan must be between 13 and 16 digits");
            }
            request.setCardNumber(decryptedPan);

            FraudCheckResponse response = transFraudService.checkAllFraudRules(request);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            success = false;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            LogHelper.logResult("fraudCheck", success, errorMsg);
        }
    }

    @GetMapping("/fraud-check/{requestId}")
    public ResponseEntity<FraudDetected> getFraud(@PathVariable String requestId) {
        log.info("GET fraud-check/{" + requestId + "}");

        FraudDetected response = fraudDetectedService.getFraud(requestId);

        if (response == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Detected fraud not found");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fraud-check")
    public ResponseEntity<List<FraudDetected>> getFrauds() {
        log.info("GET fraud-check");

        List<FraudDetected> response = fraudDetectedService.listFrauds();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
