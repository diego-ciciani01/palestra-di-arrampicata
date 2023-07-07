package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.model.Utente;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Data
public class RequestLogin {
    @NotNull(message = "L'username non può essere nullo")
    @NotBlank(message = "L'username non può essere vuoto")
    private String username;

    @NotNull(message = "La password non può essere nullq")
    @NotBlank(message = "La password  non può essere vuoto")
    @Size(min = 32, max=32, message = "La password deve essere lunga 32 caratteri, codifica in MD5")
    private String password;

    public RequestLogin(String username, String password){
        this.password = password;
        this.username = username;
    }
    public static RequestLogin parserUser(Utente utente){
        return new RequestLogin(utente.getUsername(), utente.getPassword());
    }

    public boolean isParametriPresenti(){return username != null && password != null;}
}

