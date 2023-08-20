package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.utils.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@RequiredArgsConstructor
@ToString
public class RequestEscursione {

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATETIME, message = Utils.ERROR_LOCALDATETIME)
    private String dataPubblicazione;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String emailOrganizzatore;

    @NotNull
    @NotEmpty
    private String postiDisponibili;

    @NotNull
    @NotEmpty
    private String descrizione;

    @NotNull
    @NotEmpty
    private String nomeEscursione;
}
