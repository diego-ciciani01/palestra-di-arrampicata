package com.polimi.palestraarrampicata.dto;

import com.polimi.palestraarrampicata.dto.response.*;
import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Lezione;
import com.polimi.palestraarrampicata.model.Palestra;
import com.polimi.palestraarrampicata.model.Utente;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;


public class DTOManager {
    private static Utente user;
    public static ResponseUtente ResponseUtenteFromUtente(Utente utente){
        return ResponseUtente.builder()
                .id(utente.getId().toString())
                .email(utente.getEmail())
                .nome(utente.getNome())
                .username(utente.getUsername())
                .fotoProfilo(utente.getFotoProfilo())
                .ruolo(utente.getRuolo())
                .build();
    }
    public static ResponseLezione toLessonResponseByLesson(Lezione lezione){
        return ResponseLezione.builder()
                .id(lezione.getId().toString())
                .dataLezione(lezione.getData().toString())
                .istruttore(lezione.getIstruttore().getNome())
                .statoLezione(lezione.getStatoLezione())
                .build();
    }
    public static ResponseAccettaRifiuta ResposeIstruttoreFromLesson(Lezione lezione){
        return ResponseAccettaRifiuta.builder()
                .id(lezione.getId().toString())
                .accettata(lezione.getStatoLezione())
                .commento(lezione.getCommento())
                .build();

    }
    public  static List<ResponseUtente> toUserResponseByUsers(List<Utente> utenti){
           List<ResponseUtente> listUtenti = new ArrayList<>();
           List<ResponseCorso> corsiUtenti = new ArrayList<>();
            for(Utente u: utenti){
                for(Corso c: u.getCorsiIscritto()) {
                    corsiUtenti.add(
                            ResponseCorso.builder()
                                    .nome(c.getNome())
                                    .id(c.getId().toString())
                                    .emailIstruttore(c.getIstruttoreCorso().getEmail())
                                    .numeroSettimane(c.getSettimaneDiCorso())
                                    .build()
                    );
                }
                listUtenti.add(
                        ResponseUtente.builder()
                                .id(u.getId().toString())
                                .nome(u.getNome())
                                .username(u.getUsername())
                                .fotoProfilo(u.getFotoProfilo())
                                .ruolo(u.getRuolo())
                                .email(u.getEmail())
                                .eta(Period.between(u.getDataDiNascita(), LocalDate.now()).getYears())
                                .corsiIscritto(corsiUtenti)
                                .build());

            }
        return listUtenti;
    }

    public static ResponsePalestra toPalestraResponseByPalestra(Palestra palestra){
        return ResponsePalestra.builder()
                .id(palestra.getId().toString())
                .nome(palestra.getNome())
                .build();
    }
}
