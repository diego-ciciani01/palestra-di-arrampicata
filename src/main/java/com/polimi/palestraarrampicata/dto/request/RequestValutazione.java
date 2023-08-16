package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.utils.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@RequiredArgsConstructor
public class RequestValutazione {

    @NotNull(message = "Il valore della valutazione non può essere vuoto")
    @Min(value = 1, message = Utils.ERROR_MIN_VALUE)
    @Max(value = 5, message = Utils.ERROR_MAX_VALUE)
    private String valore;

    @NotNull(message = "L'email non può essere vuota, serve per indicare quele istruttore si vuole valutare")
    @Pattern(regexp= Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String emailValutato;

}
