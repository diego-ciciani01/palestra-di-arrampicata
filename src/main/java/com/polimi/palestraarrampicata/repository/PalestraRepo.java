package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Palestra;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PalestraRepo extends CrudRepository<Palestra, Integer> {
    List<Palestra> findAll();

    Optional<Palestra> findByNome(String nomePalestra);

    Optional<Palestra> findByNomeAndIndirizzo(String nomePalestra, String indirizzo);

    Optional<Palestra> findByNomeAndEmailPalestra(String nomePalestra, String Email);

    Optional<Palestra> findByEmailPalestra(String email);




}
