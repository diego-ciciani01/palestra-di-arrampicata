package com.polimi.palestraarrampicata.exception;

public class ModificaFallita extends RuntimeException{

    public ModificaFallita(String message) {
        super(message);
    }

    public ModificaFallita() {
        super("Username o Email gia in uso");
    }
}
