package com.polimi.palestraarrampicata.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
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
