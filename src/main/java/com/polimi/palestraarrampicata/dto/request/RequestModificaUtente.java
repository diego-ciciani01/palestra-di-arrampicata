package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestModificaUtente {

    @NotNull
    @NotEmpty
    private String nome;

    @NotEmpty
    @NotNull
    private String cognome;

    @NotEmpty
    @Pattern(regexp= Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private  String email;

    private String fotoProfilo;

    @Pattern(regexp = Utils.REGEX_PASSWORD, message = Utils.ERROR_PASSWORD)
    private String password;


}
