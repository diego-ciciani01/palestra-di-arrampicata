package com.polimi.palestraarrampicata.dto.response;

import com.polimi.palestraarrampicata.model.Utente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ResposeLezione {
    private String dataLezione;
    private String id;
    private boolean statoLezione;
    private String istruttore;
    private List <Utente> iscritti;
}
