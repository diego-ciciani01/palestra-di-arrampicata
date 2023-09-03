package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Noleggio;
import com.polimi.palestraarrampicata.model.Utente;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NoleggioRepo extends CrudRepository <Noleggio,Integer>{


}
