package com.polimi.palestraarrampicata.exception;

public class RegistrazioneFallita extends RuntimeException{
    public RegistrazioneFallita(String messaggio){
        super(messaggio);
    }
}
