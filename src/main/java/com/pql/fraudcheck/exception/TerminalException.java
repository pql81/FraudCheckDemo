package com.pql.fraudcheck.exception;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
public class TerminalException extends RuntimeException {

    public TerminalException(String message) {
        super(message);
    }

    public TerminalException(String message, Throwable cause) {
        super(message, cause);
    }
}
