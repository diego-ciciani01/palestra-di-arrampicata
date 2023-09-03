package com.polimi.palestraarrampicata.exception;

public class NoleggioFallito extends RuntimeException{

    public NoleggioFallito(String messaggio){
        super(messaggio);
    }
}
