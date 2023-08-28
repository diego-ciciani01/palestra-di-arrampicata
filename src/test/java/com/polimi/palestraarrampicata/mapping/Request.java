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

}
