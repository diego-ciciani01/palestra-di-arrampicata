package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.utils.Utils;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class RequestRegistrazione {

    @NotNull
    @NotEmpty
    private String nome;

    @NotNull
    @NotEmpty
    private String cognome;

    @NotNull
    @NotEmpty
    @Pattern(regexp="^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "Inserire un indirizzo email valido")
    private String email;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_PASSWORD, message = Utils.ERROR_PASSWORD)
    private String password;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "dd/MM/yyyy", message = "formato della data non è valido")
    private String dataNascita;

    @NotNull
    @NotEmpty
    private String ruolo;

    private String fotoProfilo;

    public  boolean isParametriPresenti(){
        return nome != null && cognome != null && email != null && password !=  null && ruolo != null;
    }

}
