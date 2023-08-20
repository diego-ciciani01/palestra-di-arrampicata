package com.polimi.palestraarrampicata.dto.request;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@ToString
public class RequestIscriviti {

    @NotBlank(message ="devi inserire l'id del corso a cui vuoi iscriverti")
    private Integer id;

}
