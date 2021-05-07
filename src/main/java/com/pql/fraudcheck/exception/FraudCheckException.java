package com.pql.fraudcheck.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "Unable to check fraud at the moment")
public class FraudCheckException extends RuntimeException {

    public FraudCheckException(String message) {
        super(message);
    }

    public FraudCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
