package com.mercadoandes.catalog.application.exception;

public class InvalidProductIdentityException extends RuntimeException {

    public InvalidProductIdentityException(String message) {
        super(message);
    }
}
