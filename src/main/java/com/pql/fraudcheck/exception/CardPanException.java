package com.pql.fraudcheck.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by pasqualericupero on 09/05/2021.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CardPanException extends RuntimeException {

    public CardPanException(String message) {
        super(message);
    }

    public CardPanException(String message, Throwable cause) {
        super(message, cause);
    }
}
