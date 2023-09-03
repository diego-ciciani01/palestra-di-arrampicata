package com.polimi.palestraarrampicata.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ResponseCorso {
    private String id;
    private String nome;
    private Integer numeroSettimane;
    private String emailIstruttore;
    private LocalDate dataInizio;

}
