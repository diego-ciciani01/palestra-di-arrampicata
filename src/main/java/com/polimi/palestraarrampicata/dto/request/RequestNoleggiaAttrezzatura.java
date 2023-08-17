package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.utils.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * questa request è invece dedicata al noleggio dell'attrezzatura disponibile
 * possono interfacciarsi a questa request si gli user che gli istruttori
 */
@Data
@RequiredArgsConstructor
@ToString
public class RequestNoleggiaAttrezzatura {

    @NotNull
    @NotEmpty
    private String nomeAttrezzo;

    @NotNull
    @NotEmpty
    private String taglia;

    @NotNull
    @NotEmpty
    private String quantita;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATETIME, message = Utils.ERROR_LOCALDATETIME)
    private String dataInizioNoleggio;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATETIME, message = Utils.ERROR_LOCALDATETIME)
    private String dataFineNoleggio;
}
