package com.polimi.palestraarrampicata.dto.request;


import com.polimi.palestraarrampicata.utils.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@RequiredArgsConstructor
public class RequestCommento {

    private String idCommentoPadre;
    @NotNull
    @NotEmpty
    private String dataPubblicazione;
    @NotNull
    @NotEmpty
    private String testo;

    @NotNull
    @NotEmpty
    @Pattern(regexp= Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String emailIstruttoreCommentato;

}
