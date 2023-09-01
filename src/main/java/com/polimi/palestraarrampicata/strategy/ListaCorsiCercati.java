package com.polimi.palestraarrampicata.strategy;

import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Difficolta;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.CorsoRepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListaCorsiCercati implements ListaCorsi{

    @Override
    public List<Corso> getListaCorsi(Utente utente, CorsoRepo corsoRepo) {
        Iterable<Corso> listaCorso = corsoRepo.findAll();
        List<Corso> corsi  = new ArrayList<>();
        for(Corso corso : listaCorso){
            if(!corso.getDataInizio().isBefore(LocalDate.now()))
                corsi.add(corso);
        }
        return corsi;
    }

    @Override
    public List<Corso> getListaCorsi(CorsoRepo corsoRepo, String utils) {
        return null;
    }

    @Override
    public List<Corso> getListaCorsi(Utente utente, CorsoRepo corsoRepo, String utils) {
        return null;
    }

}
