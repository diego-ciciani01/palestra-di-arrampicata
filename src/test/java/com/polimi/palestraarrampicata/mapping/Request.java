package com.polimi.palestraarrampicata.mapping;

import com.polimi.palestraarrampicata.dto.request.*;
import com.polimi.palestraarrampicata.model.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class Request {

    public static RequestCommento toRequestCommentoByCommentoMapper(Commento commento){
        RequestCommento requestCommento = new RequestCommento();
        requestCommento.setEmailIstruttoreCommentato(commento.getIstruttoreCommentato().getEmail());
        requestCommento.setTesto(commento.getTesto());

        return requestCommento;
    }

    public static RequestValutazione toRequestValutazioneByValutazioneMapper(Valutazione valutazione){
        RequestValutazione requestValutazione = new RequestValutazione();
        requestValutazione.setValore(valutazione.getValore().toString());
        requestValutazione.setEmailValutato(valutazione.getValutato().getEmail());

        return  requestValutazione;
    }

    public static RequestCorso toRequestCorsoByCorsoMapper(Corso corso){
        RequestCorso requestCorso = new RequestCorso();
        requestCorso.setCosto(corso.getCosto().toString());
        requestCorso.setNomeCorso(corso.getNome());
        requestCorso.setDifficolta(corso.getDifficolta().toString());
        requestCorso.setEmailIstruttore(corso.getIstruttoreCorso().getEmail());
        requestCorso.setDataDiInizio("12/03/2023");
        return  requestCorso;
    }
    public static RequestPalestra toRequestPalestraByPalestraMapper(Palestra palestra){
        RequestPalestra  requestPalestra = new RequestPalestra();
        requestPalestra.setNomePalestra(palestra.getNome());
        requestPalestra.setCap(palestra.getCap());
        requestPalestra.setEmailPalestra(palestra.getEmailPalestra());
        requestPalestra.setTelefono(palestra.getTelefono());

        return  requestPalestra;
    }

    public static RequestIscrivitiPalestra toRequestIscrivitiPalestrabyIscrivitiPalestraMapper(Palestra palestra){
        RequestIscrivitiPalestra requestIscrivitiPalestra = new RequestIscrivitiPalestra();
        requestIscrivitiPalestra.setCitta(palestra.getCitta());
        requestIscrivitiPalestra.setEmailPalestra(palestra.getEmailPalestra());
        requestIscrivitiPalestra.setNomePalestra(palestra.getCitta());

        return requestIscrivitiPalestra;
    }

    public static RequestEscursione toRequestEscursioneByEscursioneMapper(Escursione escursione){
        RequestEscursione requestEscursione = new RequestEscursione();
        requestEscursione.setNomeEscursione(escursione.getNomeEscursione().toString());
        requestEscursione.setDescrizione(escursione.getDescrizione());
        requestEscursione.setPostiDisponibili(escursione.getPostiDisponibili().toString());
        requestEscursione.setDataPubblicazione(escursione.getData().toString());

        return requestEscursione;
    }
    public  static RequestModificaUtente toRequestModificaUtenteByUtenteMapper(Utente utente){
        RequestModificaUtente requestModificaUtente = new RequestModificaUtente();
        requestModificaUtente.setNome(utente.getNome());
        requestModificaUtente.setEmail(utente.getEmail());
        requestModificaUtente.setPassword(utente.getPassword());
        requestModificaUtente.setCognome(utente.getCognome());

        return requestModificaUtente;

    }

    public static RequestLezione toRequestLezioneByLezioneMapper(Lezione lezione){
        RequestLezione requestLezione = new RequestLezione();
        requestLezione.setTipologiaLezione(lezione.getTipologiaLezione().toString());
        requestLezione.setDuration(lezione.getData().toString());
        requestLezione.setStartLesson(lezione.getData().toString());

        return requestLezione;
    }

    public static RequestAccettaRiffiuta toRequestAcettaRifiutaByLezioneMapper(Lezione lezione){
        RequestAccettaRiffiuta requestAccettaRiffiuta = new RequestAccettaRiffiuta();
        requestAccettaRiffiuta.setIdLezione(lezione.getId().toString());
        requestAccettaRiffiuta.setAccetta("true");
        requestAccettaRiffiuta.setCommento("lezione accettata");

        return requestAccettaRiffiuta;
    }

    public  static RequestModificaEscursione toRequestEscursioneByRequestModificaEscursione(Escursione escursione){
        RequestModificaEscursione requestModificaEscursione = new RequestModificaEscursione();
        requestModificaEscursione.setId(escursione.getId().toString());
        requestModificaEscursione.setData(escursione.getData().toString());
        requestModificaEscursione.setStatoIscrizione(escursione.getStatoEscursione().toString());
        requestModificaEscursione.setNomeEscursione(escursione.getNomeEscursione());
        requestModificaEscursione.setDescrizione(escursione.getDescrizione());
        requestModificaEscursione.setPostiDisponibili(escursione.getPostiDisponibili().toString());

        return requestModificaEscursione;

    }

}
