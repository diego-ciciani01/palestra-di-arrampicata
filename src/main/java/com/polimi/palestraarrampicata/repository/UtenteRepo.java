package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Utente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepo extends CrudRepository<Utente, Integer> {
    Optional<Utente> findUserByEmail(String username);
}

