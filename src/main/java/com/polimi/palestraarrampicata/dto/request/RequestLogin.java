package com.polimi.palestraarrampicata.dto.request;

import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestLogin {

    @NotNull
    @Pattern(regexp= Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String email;

    @NotNull
    @NotEmpty
    private String password;


}

