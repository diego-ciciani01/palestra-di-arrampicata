package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.utils.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * questa request è destinata all'utilizzo dell'amministratore della piattaforma
 * perché è lunico che dispone dei poteri per poter effettuare l'inserimento di
 * nuovi attrezzi all'interno del magazzino
 */
@Data
@RequiredArgsConstructor
@ToString
public class RequestAttrezzatura {
    @NotNull
    @NotEmpty
    private String quantita;

    @NotNull
    @NotEmpty
    private String disponibilita;

    @NotNull
    @NotEmpty
    private String nomeAttrezzo;

    @NotNull
    @NotEmpty
    private String taglia;

    @NotNull
    @NotEmpty
    private String idPalestraPossessore;

}
