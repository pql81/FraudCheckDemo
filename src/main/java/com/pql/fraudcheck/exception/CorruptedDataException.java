package com.pql.fraudcheck.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Data corrupted during processing")
public class CorruptedDataException extends RuntimeException {

    public CorruptedDataException(String message) {
        super(message);
    }

    public CorruptedDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
