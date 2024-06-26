package org.transformerservice.exceptions;

public class InvalidTransformerConfigurationException extends RuntimeException {

    public InvalidTransformerConfigurationException(String message) {
        super(message);
    }

    public InvalidTransformerConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
