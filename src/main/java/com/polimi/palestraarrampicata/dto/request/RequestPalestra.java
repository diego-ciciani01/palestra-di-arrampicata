package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
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
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String emailPalestra;

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
