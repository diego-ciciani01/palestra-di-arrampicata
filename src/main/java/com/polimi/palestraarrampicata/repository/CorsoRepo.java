package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Utente;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CorsoRepo extends CrudRepository <Corso, Integer> {

    List <Corso> findAllByIstruttoreCorso(Utente istruttore);


}
