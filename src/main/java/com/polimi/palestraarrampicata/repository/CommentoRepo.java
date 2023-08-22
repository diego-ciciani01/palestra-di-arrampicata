package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Commento;
import com.polimi.palestraarrampicata.model.Utente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentoRepo extends CrudRepository<Commento, Integer> {
    List<Commento> findAllByCommentatoreAndIstruttoreCommentato(Utente commentatore, Utente istruttore);
}
