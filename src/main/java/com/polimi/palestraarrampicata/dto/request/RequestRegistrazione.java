package com.polimi.palestraarrampicata.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RequestRegistrazione {

    @NotBlank(message = "il nome non può essere vuoto")
    private String nome;

    @NotBlank(message = "il cognome non può essere vuoto")
    private String cognome;

    @NotBlank(message = "l'username non può essere vuoto")
    private String username;

    @NotBlank(message = "l'email non può essere vuoto")
    @Pattern(regexp="^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "Inserire un indirizzo email valido")
    private String email;

    @NotBlank(message = "la password non può essere vuota")
    @Size(min = 32, max = 32, message = "L'hash della password deve essere lungo 32 caratteri (MD5 hash)")
    private String password;

    @NotBlank(message = "la data di nascita non può essere vuota")
    private String dataNascita;

    @NotBlank(message = "il ruolo non può essere vuoto")
    private String ruolo;

    private String fotoProfilo;

    public  boolean isParametriPresenti(){
        return nome != null && cognome != null && username != null && email != null && password !=  null && ruolo != null;
    }

}
