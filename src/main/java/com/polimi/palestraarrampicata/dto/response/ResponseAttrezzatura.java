package com.polimi.palestraarrampicata.dto.response;

import com.polimi.palestraarrampicata.model.Taglia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ResponseAttrezzatura {
    private  String id;
    private String nomePalestraAppartenente;
    private boolean disponibilita;
    private Integer quantitaDisponibile;
    private LocalDateTime dataFineNoleggio;
    private LocalDateTime dataNoleggio;
    private String nomeAttrezzo;
    private List<Taglia> taglia;
}
