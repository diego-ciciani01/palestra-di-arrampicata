package com.polimi.palestraarrampicata.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseNoleggio {
    private String dataInizioNoleggio;
    private String dataFineNoleggio;
    private String id;

}
