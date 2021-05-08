package com.pql.fraudcheck.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid currency ISO code")
public class CurrencyException  extends RuntimeException {

    public CurrencyException(String message) {
        super(message);
    }

    public CurrencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
