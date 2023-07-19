package com.polimi.palestraarrampicata.dto.request;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
@Data
public class RequestModificaUtente {
    private String nome;

    private String cognome;

    private String username;

    @Pattern(regexp="^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "Inserire un indirizzo email valido")
    private  String email;

    private String fotoProfilo;
    @Size(min = 32, max = 32, message = "L'hash della password deve essere lungo 32 caratteri (MD5 hash)")
    private String password;

    public boolean isEmpty() {
        return  nome == null &&
                cognome == null &&
                username == null &&
                email == null &&
                password == null &&
                fotoProfilo == null;
    }
}
