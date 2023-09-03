package com.polimi.palestraarrampicata.mapping;
import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.response.ResponseCommento;
import com.polimi.palestraarrampicata.dto.response.ResponseValutazione;
import com.polimi.palestraarrampicata.model.Commento;
import com.polimi.palestraarrampicata.model.Valutazione;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Response {

    public static ResponseValutazione toValutazioneResponseByValutazioneMapper(Valutazione valutazione){
        return ResponseValutazione.builder()
                .id(valutazione.getId().toString())
                .emailIstruttoreValutato(valutazione.getValutato().getEmail())
                .valore(valutazione.getValore().toString())
                .build();
    }

    public static List<ResponseCommento> toCommentoListResponseByCommentoListMapper(List<Commento> commenti){
        List<ResponseCommento> responseCommentiList = new ArrayList<>();
        for(Commento c: commenti){
            responseCommentiList.add(
                    ResponseCommento.builder()
                            .commentatore(c.getCommentatore().getEmail())
                            .testo(c.getTesto())
                            .emailIstruttore(c.getIstruttoreCommentato().getEmail())
                            .id(c.getId().toString())
                            .build());
        }
        return responseCommentiList;

    }


}
