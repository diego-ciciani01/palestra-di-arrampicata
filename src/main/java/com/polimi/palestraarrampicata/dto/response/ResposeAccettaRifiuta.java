package com.polimi.palestraarrampicata.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResposeAccettaRifiuta {
    private String id;
    private String commento;
    private boolean accettata;
}
