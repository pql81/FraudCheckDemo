package com.pql.fraudcheck.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by pasqualericupero on 16/05/2021.
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "JWT token missing/expired/invalid")
public class UnauthorizedAPIException extends RuntimeException {

    public UnauthorizedAPIException(String message) {
        super(message);
    }

    public UnauthorizedAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
