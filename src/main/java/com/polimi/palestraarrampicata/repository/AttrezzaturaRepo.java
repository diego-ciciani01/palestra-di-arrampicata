package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Attrezzatura;
import com.polimi.palestraarrampicata.model.Taglia;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AttrezzaturaRepo extends CrudRepository<Attrezzatura, Integer> {
     List<Attrezzatura> findAllByDisponibilita(boolean disponibilita);

     Attrezzatura findByNomeAttrezzatura(String nome);


}
