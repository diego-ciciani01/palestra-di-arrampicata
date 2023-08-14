package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.model.TipologiaLezione;
import com.polimi.palestraarrampicata.utils.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@RequiredArgsConstructor
@ToString
public class RequestLezione {

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATETIME, message = Utils.ERROR_LOCALDATETIME)
    private String startLesson;

    @NotNull
    @NotEmpty
    private String duration;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String instructorEmail;

    @NotBlank(message = "la lezione deve avere una tipologia, specificare il tipo di lezione")
    private String tipologiaLezione;


}
