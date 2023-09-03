package com.polimi.palestraarrampicata.strategy;

import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.CorsoRepo;

import java.util.List;

public class ContextListaCorsi {

    private ListaCorsi listaCorsi;

    public ContextListaCorsi(){}


    public void eseguiStrategiaListaCorso(ListaCorsi listaCorsi){
        this.listaCorsi = listaCorsi;
    }

    public List<Corso> eseguiRicerca(Utente utente, CorsoRepo corsoRepo, String  utils){

        return listaCorsi.getListaCorsi(utente, corsoRepo, utils);
    }

    public List<Corso> eseguiRicerca(Utente utente, CorsoRepo corsoRepo){

        return listaCorsi.getListaCorsi(utente, corsoRepo);
    }

    public List<Corso> eseguiRicerca(CorsoRepo corsoRepo, String utils){

        return listaCorsi.getListaCorsi(corsoRepo, utils);
    }

}
