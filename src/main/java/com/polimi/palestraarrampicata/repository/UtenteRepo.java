package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteRepo extends CrudRepository<Utente, Integer> {
    Optional<Utente> findUserByEmail(String username);

    Optional <Utente> findByEmail(String email);

    Optional<Utente> findUserByEmailAndRuolo(String email, Ruolo istruttore);

    List<Corso> findCorsiTenutiByEmail(String email);

    Optional<Utente> findByIscrittiPalestra(Palestra palestra);

}

