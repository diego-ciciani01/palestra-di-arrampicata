package com.polimi.palestraarrampicata.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseValutazione {
    private String id;
    private String valore;
    private String emailIstruttoreValutato;

}
