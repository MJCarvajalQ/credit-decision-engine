package com.decisionengine.exception;

public class NoValidLoanException extends RuntimeException {

    public NoValidLoanException(String message) {
        super(message);
    }
}
