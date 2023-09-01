package com.polimi.palestraarrampicata.strategy;

import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Difficolta;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.CorsoRepo;

import java.time.LocalDate;
import java.util.List;

public class ListaCorsiIstruttore implements ListaCorsi{
    @Override
    public List<Corso> getListaCorsi(Utente utente, CorsoRepo corsoRepo) {
        List<Corso> listaCorso = utente.getCorsiTenuti();
        listaCorso.removeIf(corso -> corso.getDataInizio().isBefore(LocalDate.now()));
        return  listaCorso;
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
