package com.polimi.palestraarrampicata.dto;

import com.polimi.palestraarrampicata.dto.response.*;
import com.polimi.palestraarrampicata.model.*;

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
    public static ResponseAccettaRifiuta ToResposeIstruttoreByLesson(Lezione lezione){
        return ResponseAccettaRifiuta.builder()
                .id(lezione.getId().toString())
                .accettata(lezione.getStatoLezione())
                .commento(lezione.getCommento())
                .build();

    }
    public static ResponseCommento ToResponseCommentoBYCommento(Commento commento){
        return ResponseCommento.builder()
                .id(commento.getId().toString())
                .emailIstruttore(commento.getIstruttoreCommentato().getEmail())
                .testo(commento.getTesto())
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
    public static ResponseAttrezzatura toAttrezzaturaResponseByAttrezzatura(Attrezzatura attrezzi){
        return ResponseAttrezzatura.builder()
                .id(attrezzi.getId().toString())
                .disponibilita(attrezzi.getDisponibilita())
                .taglia(attrezzi.getNomeTaglia())
                .dataNoleggio(attrezzi.getDataNoleggio())
                .dataFineNoleggio(attrezzi.getDataFineNoleggio())
                .nomeAttrezzo(attrezzi.getNomeAttrezzatura())
                .nomePalestraAppartenente(attrezzi.getAttrezziPalestra().getNome())
                .build();
    }
}
