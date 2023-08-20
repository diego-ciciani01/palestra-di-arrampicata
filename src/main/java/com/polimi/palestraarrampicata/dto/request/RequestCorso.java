package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.model.Difficolta;
import com.polimi.palestraarrampicata.utils.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@ToString
public class RequestCorso {

    @NotBlank(message = "il corso deve avere un costo")
    @Min(value = 0, message = "il costo deve essere maggiore o uguale a 0")
    private String costo;

    @NotBlank(message = "L'attività deve avere una difficoltà")
    private String difficolta;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String emailIstruttore;

    @NotBlank(message = "il corso deve avere un numero di settimane")
    private String numeroSettimane;

    @NotNull
    @NotEmpty
    private String nomeCorso;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATE, message = Utils.ERROR_LOCALDATE)
    private String dataDiInizio;

}
