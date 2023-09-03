package com.polimi.palestraarrampicata.strategy;

import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Palestra;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.CorsoRepo;

import java.util.ArrayList;
import java.util.List;

public class ListaCorsiPalestraIscritto implements ListaCorsi{
    @Override
    public List<Corso> getListaCorsi(Utente utente, CorsoRepo corsoRepo, String utils) {
        return null;
    }

    @Override
    public List<Corso> getListaCorsi(Utente utente, CorsoRepo corsoRepo) {
        Palestra palestraIscritto = utente.getIscrittiPalestra();
        Iterable<Corso> corsi = corsoRepo.findAll();
        List<Corso> corsiVisibiliUtente = new ArrayList<>();
        for(Corso corso : corsi){
            if(corso.getCorsoPalestra().equals(palestraIscritto))
                corsiVisibiliUtente.add(corso);
        }

        return corsiVisibiliUtente;
    }

    @Override
    public List<Corso> getListaCorsi(CorsoRepo corsoRepo, String utils) {
        return null;
    }
}
