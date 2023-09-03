package com.polimi.palestraarrampicata.strategy;

import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Difficolta;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.CorsoRepo;

import java.util.List;

public interface ListaCorsi {

    List<Corso> getListaCorsi (Utente utente, CorsoRepo corsoRepo, String utils);
    List<Corso> getListaCorsi (Utente utente, CorsoRepo corsoRepo);

    List<Corso> getListaCorsi (CorsoRepo corsoRepo, String utils);

}
