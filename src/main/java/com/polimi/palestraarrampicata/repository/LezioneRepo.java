package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Lezione;
import com.polimi.palestraarrampicata.model.Utente;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LezioneRepo extends CrudRepository<Lezione, Integer> {

    List<Lezione> findAll();

    List<Lezione> findAllByIstruttore(Utente istruttore);

    List<Lezione> findAllByStatoLezione(boolean statoLezione);
}
