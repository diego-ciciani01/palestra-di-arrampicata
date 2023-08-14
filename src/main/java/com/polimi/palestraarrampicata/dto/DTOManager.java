package com.polimi.palestraarrampicata.dto;

import com.polimi.palestraarrampicata.dto.response.ResposeAccettaRifiuta;
import com.polimi.palestraarrampicata.dto.response.ResposeLezione;
import com.polimi.palestraarrampicata.dto.response.ResposeUtente;
import com.polimi.palestraarrampicata.model.Lezione;
import com.polimi.palestraarrampicata.model.Utente;
import lombok.Builder;


public class DTOManager {
    private static Utente user;
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

    public static ResposeLezione toLessonResponseByLesson(Lezione lezione){
        return ResposeLezione.builder()
                .id(lezione.getId().toString())
                .dataLezione(lezione.getData().toString())
                .istruttore(lezione.getIstruttore().getNome())
                .statoLezione(lezione.getStatoLezione())
                .build();
    }

    public static ResposeAccettaRifiuta ResposeIstruttoreFromLesson(Lezione lezione){
        return ResposeAccettaRifiuta.builder()
                .id(lezione.getId().toString())
                .accettata(lezione.getStatoLezione())
                .commento(lezione.getCommento())
                .build();

    }
}
