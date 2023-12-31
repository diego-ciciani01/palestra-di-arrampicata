package com.polimi.palestraarrampicata.dto.request;
import com.polimi.palestraarrampicata.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class RequestIscrivitiPalestra {

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String emailPalestra;

    @NotNull
    @NotEmpty
    private String citta;

    @NotNull
    @NotEmpty
    private String nomePalestra;


}
