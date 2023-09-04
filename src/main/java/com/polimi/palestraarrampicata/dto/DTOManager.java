package com.polimi.palestraarrampicata.dto;

import com.polimi.palestraarrampicata.dto.response.*;
import com.polimi.palestraarrampicata.model.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;


public class DTOManager {
    public static ResponseUtente ResponseUtenteFromUtente(Utente utente){
        return ResponseUtente.builder()
                .id(utente.getId().toString())
                .email(utente.getEmail())
                .nome(utente.getNome())
                .username(utente.getUsername())
                .fotoProfilo(utente.getFotoProfilo())
                .ruolo(utente.getRuolo())
                .eta(Period.between(utente.getDataDiNascita(), LocalDate.now()).getYears())
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
    public static ResponseCommento ToResponseCommentoByCommento(Commento commento){
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
                                .fotoProfilo(u.getFotoProfilo())
                                .ruolo(u.getRuolo())
                                .email(u.getEmail())
                                .eta(Period.between(u.getDataDiNascita(), LocalDate.now()).getYears())
                                .corsiIscritto(corsiUtenti)
                                .build());

            }
        return listUtenti;
    }

    public static List<ResponseAttrezzatura> listAttrezzatureWithNoleggio(List<Attrezzatura> attrezzi){
        List<ResponseAttrezzatura> listAttrezzatura = new ArrayList<>();
        List<ResponseNoleggio> listNoleggio = new ArrayList<>();
        for(Attrezzatura attrezzo: attrezzi){
            for(Noleggio noleggio: attrezzo.getNoleggi()){
                listNoleggio.add(
                        ResponseNoleggio.builder()
                                .id(noleggio.getId().toString())
                                .dataInizioNoleggio(noleggio.getDataNoleggio().toString())
                                .dataFineNoleggio(noleggio.getDataFineNoleggio().toString())
                                .build());
            }
            listAttrezzatura.add(
                    ResponseAttrezzatura.builder()
                            .id(attrezzo.getId().toString())
                            .nomePalestraAppartenente(attrezzo.getAttrezziPalestra().getNome())
                            .taglia(attrezzo.getNomeTaglia())
                            .nomeAttrezzo(attrezzo.getNomeAttrezzatura())
                            .disponibilita(attrezzo.getDisponibilita())
                            .noleggi(listNoleggio)
                            .build());
        }
        return listAttrezzatura;
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
                .nomeAttrezzo(attrezzi.getNomeAttrezzatura())
                .nomePalestraAppartenente(attrezzi.getAttrezziPalestra().getNome())
                .build();
    }

    public static ResponseCorso toCorsoResponseByCorso(Corso corso){
       return ResponseCorso.builder()
               .id(corso.getId().toString())
               .numeroSettimane(corso.getSettimaneDiCorso())
               .nome(corso.getNome())
               .emailIstruttore(corso.getIstruttoreCorso().getEmail())
               .dataInizio(corso.getDataInizio())
               .build();
    }

    public static ResponseEscursione toEscursioneResponseByEscursione(Escursione escursione){
        return ResponseEscursione.builder()
                .id(escursione.getId())
                .nomeEscursione(escursione.getNomeEscursione())
                .data(escursione.getData())
                .descrizione(escursione.getDescrizione())
                .emailOrganizzatore(escursione.getOrganizzatore().getEmail())
                .postiDisponibili(escursione.getPostiDisponibili())
                .build();
    }
}
