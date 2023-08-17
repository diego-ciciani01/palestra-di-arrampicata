package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.model.Valutazione;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface ValutazioneRepo extends CrudRepository<Valutazione, Integer> {

    Valutazione findByValutatoreAndValutato(Utente valutatore, Utente valutato);
}
