package com.polimi.palestraarrampicata.dto;

import com.polimi.palestraarrampicata.dto.response.ResposeUtente;
import com.polimi.palestraarrampicata.model.Utente;
import lombok.Builder;


public class DTOManager {
    public static ResposeUtente ResponseUtenteFromUtente(Utente utente){
        return ResposeUtente.builder()
                .id(utente.getId().toString())
                .email(utente.getEmail())
                .nome(utente.getNome())
                .username(utente.getUsername())
                .fotoProfilo(utente.getFotoProfilo())
                .ruolo(utente.getRuolo())
                .build();
    }
}
