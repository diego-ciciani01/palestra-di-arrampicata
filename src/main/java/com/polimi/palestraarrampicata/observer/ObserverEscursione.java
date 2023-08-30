package com.polimi.palestraarrampicata.observer;

import com.polimi.palestraarrampicata.model.Escursione;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.EscursioniRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class ObserverEscursione implements Observer{
    @Autowired
    private EscursioniRepo escursioniRepo;
    @Autowired
    private UtenteRepo utenteRepo;


    @Override
    public void update() {
        List<Escursione> listaEscursioniInCorso = escursioniRepo.findAllByStatoEscursione(true);

        for(Escursione escursione: listaEscursioniInCorso){
            if(escursione.getData().isBefore(LocalDateTime.now())){
                escursione.setStatoEscursione(false);

                for (Utente utentePartecipante:escursione.getUtentiPartecipanti()){
                    utentePartecipante.getEscursioniPartecipate().remove(escursione);
                    utenteRepo.save(utentePartecipante);
                }
                escursioniRepo.save(escursione);
            }
        }
    }
}
