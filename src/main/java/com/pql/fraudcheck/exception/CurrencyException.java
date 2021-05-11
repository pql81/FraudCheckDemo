package com.pql.fraudcheck.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class CurrencyException  extends RuntimeException {

    public CurrencyException(String message) {
        super(message);
    }

    public CurrencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
