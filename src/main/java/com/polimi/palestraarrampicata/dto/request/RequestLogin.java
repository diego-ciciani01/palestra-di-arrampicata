package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.model.Utente;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
public class RequestLogin {

    @NotNull
    @Pattern(regexp="^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "Inserire un indirizzo email valido")
    private String email;

    @NotNull
    @NotEmpty
    private String password;


}

