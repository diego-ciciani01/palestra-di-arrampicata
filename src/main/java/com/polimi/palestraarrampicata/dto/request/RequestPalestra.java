package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class RequestPalestra {

    @NotNull
    @NotEmpty
    private String indirizzo;

    @NotNull
    @NotEmpty
    @Size(min = 5, max = 5, message = Utils.ERROR_CAP)
    private String cap;

    @NotNull
    @NotEmpty
    private String citta;

    @NotNull
    @NotEmpty
    private String nomePalestra;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_TELEFONO, message = Utils.ERROR_TELEFONO)
    private String telefono;



}
