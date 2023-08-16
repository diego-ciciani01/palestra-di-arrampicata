package com.polimi.palestraarrampicata.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseCommento {
    private String id;
    private String testo;
    private String emailIstruttore;

}
