package com.polimi.palestraarrampicata.dto.response;

import com.polimi.palestraarrampicata.model.Utente;

public class ResponseLogin {
    private ResposeUtente utente;

    public ResponseLogin(Utente utente){
        this.utente = new ResposeUtente(utente);
    }
}
