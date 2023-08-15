package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Commento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentoRepo extends CrudRepository<Commento, Integer> {

}
