package com.polimi.palestraarrampicata.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor
@ToString
public class RequestModificaEscursione {
    private String id;
    private String data;
    private String postiDisponibili;
    private String descrizione;
    private String statoIscrizione;
    private String nomeEscursione;

}
