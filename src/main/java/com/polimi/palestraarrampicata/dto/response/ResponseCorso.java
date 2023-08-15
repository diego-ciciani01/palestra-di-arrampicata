package com.polimi.palestraarrampicata.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseCorso {
    private String id;
    private String nome;
    private Integer numeroSettimane;
    private String emailIstruttore;

}
