package com.polimi.palestraarrampicata.dto.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RequestAccettaRiffiuta {

    @NotNull
    @NotEmpty
    private String idLezione;

    @NotNull
    @NotEmpty
    private String accetta;

    @NotNull
    @NotEmpty
    private String commento;
}
