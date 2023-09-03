package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.model.Valutazione;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValutazioneRepo extends CrudRepository<Valutazione, Integer> {

    Valutazione findByValutatoreAndValutato(Utente valutatore, Utente valutato);
    List<Valutazione> findAllByValutato(Utente valutato);

}
