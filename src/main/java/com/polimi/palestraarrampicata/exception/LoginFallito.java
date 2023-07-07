package com.polimi.palestraarrampicata.exception;

public class LoginFallito extends RuntimeException {
    public LoginFallito(String message) {
        super(message);
    }

    public LoginFallito() {
        super("Credenziali non valide: login fallito.");
    }
}
