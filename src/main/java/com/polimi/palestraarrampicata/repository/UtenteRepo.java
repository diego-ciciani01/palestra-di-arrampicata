package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtenteRepo extends JpaRepository<Utente, Integer> {
    Utente findByUsernameOrEmail(String username, String email);

    Utente findByUsername(String username);
}

