package com.polimi.palestraarrampicata.strategy;

import com.polimi.palestraarrampicata.exception.CreazioneAttivitaFallita;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Difficolta;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.CorsoRepo;

import java.util.ArrayList;
import java.util.List;

public class ListaCorsiPerDifficolta implements ListaCorsi{


    @Override
    public List<Corso> getListaCorsi(Utente utente, CorsoRepo corsoRepo, String utils) {
        return  null;
    }

    @Override
    public List<Corso> getListaCorsi(Utente utente, CorsoRepo corsoRepo) {
        return null;
    }

    @Override
    public List<Corso> getListaCorsi(CorsoRepo corsoRepo, String utils) {
        Iterable<Corso> listaCorso = corsoRepo.findAll();
        List<Corso> corsiOrdinatiPerDifficolta = new ArrayList<>();
        Difficolta difficolta;
        try{
            // Converte la stringa della difficoltà in un enum Difficolta
            difficolta = Difficolta.fromString(utils);
        }catch (IllegalArgumentException e) {
            throw new RicercaFallita("La difficoltà inserita non è valida");
        }
        for(Corso corso:listaCorso){
            if(corso.getDifficolta().equals(difficolta))
                corsiOrdinatiPerDifficolta.add(corso);
        }
        return corsiOrdinatiPerDifficolta;
    }
}
