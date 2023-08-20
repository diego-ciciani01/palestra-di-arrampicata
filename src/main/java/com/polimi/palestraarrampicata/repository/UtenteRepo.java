package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Lezione;
import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.Utente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteRepo extends CrudRepository<Utente, Integer> {
    Optional<Utente> findUserByEmail(String username);

    Utente findByEmail(String email);

    Optional<Utente> findUserByEmailAndRuolo(String email, Ruolo istruttore);

    List<Corso> findCorsiTenutiByEmail(String email);

}

