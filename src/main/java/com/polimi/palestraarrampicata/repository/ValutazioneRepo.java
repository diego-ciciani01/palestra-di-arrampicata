package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.model.Valutazione;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValutazioneRepo extends CrudRepository<Valutazione, Integer> {

    Valutazione findByValutatoreAndValutato(Utente valutatore, Utente valutato);
/*
    @Query("SELECT AVG(v.valore) FROM valutazione v WHERE v.valutato.id =:id")
    double getValutazioneOrganizzatore(@Param("valutato") int id);

 */
}
