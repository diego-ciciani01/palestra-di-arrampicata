package com.polimi.palestraarrampicata.dto.response;

import com.polimi.palestraarrampicata.model.Commento;
import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.Utente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResposeUtente {
    private String id;
    private String nome;
    private String username;
    private  byte[] fotoProfilo;
    private Ruolo ruolo;
    private  String email;

}
