package com.polimi.palestraarrampicata.dto.response;

import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Ruolo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ResponseUtente {
    private String id;
    private String nome;
    private String username;
    private  byte[] fotoProfilo;
    private Ruolo ruolo;
    private  String email;
    private List<ResponseCorso> corsiIscritto;
    private Integer eta;

}
