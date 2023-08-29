package com.polimi.palestraarrampicata.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseEscursione {
    private Integer id;
    private String emailOrganizzatore;
    private Integer postiDisponibili;
    private String nomeEscursione;
    private String descrizione;
    private LocalDateTime data;
}
