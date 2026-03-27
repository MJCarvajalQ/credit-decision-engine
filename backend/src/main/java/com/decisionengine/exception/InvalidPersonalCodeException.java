package com.decisionengine.exception;

public class InvalidPersonalCodeException extends RuntimeException {

    public InvalidPersonalCodeException(String personalCode) {
        super("Unknown personal code: " + personalCode);
    }
}
