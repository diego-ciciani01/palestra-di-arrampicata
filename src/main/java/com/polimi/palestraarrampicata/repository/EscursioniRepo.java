package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Escursione;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EscursioniRepo extends CrudRepository<Escursione, Integer> {
    List<Escursione> findAllByStatoEscursione(boolean status);

}
