package com.pql.fraudcheck.controller;

import com.pql.fraudcheck.dto.TerminalLocationResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pasqualericupero on 09/05/2021.
 */
@Log4j2
@RestController
public class MockTerminalController {

    @GetMapping("/mock/terminal/{terminalId}/transactions")
    public ResponseEntity<Integer> trans(@PathVariable String terminalId) {

        if (!terminalId.startsWith("T")) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Integer transNum;
            if (terminalId.endsWith("04")) {
                transNum = 530;
            } else if (terminalId.endsWith("05")) {
                transNum = 1230;
            } else {
                transNum = 132;
            }

            return new ResponseEntity<>(transNum, HttpStatus.OK);
        }
    }

    @GetMapping("/mock/terminal/{terminalId}/last-location")
    public ResponseEntity<TerminalLocationResponse> location(@PathVariable String terminalId) {

        if (!terminalId.startsWith("T")) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new TerminalLocationResponse(-1.284, 2.324), HttpStatus.OK);
        }
    }
}
