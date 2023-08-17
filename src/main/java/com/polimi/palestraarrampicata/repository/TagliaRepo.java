package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Taglia;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TagliaRepo extends CrudRepository<Taglia,Integer> {

    List<Taglia> findByNomeTaglia(String taglia);
}
