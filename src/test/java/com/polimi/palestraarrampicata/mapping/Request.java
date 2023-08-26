package com.polimi.palestraarrampicata.mapping;

import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.request.RequestValutazione;
import com.polimi.palestraarrampicata.model.Commento;

import com.polimi.palestraarrampicata.model.Valutazione;
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

}
