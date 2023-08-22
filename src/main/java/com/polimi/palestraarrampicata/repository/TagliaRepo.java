package com.polimi.palestraarrampicata.repository;

import com.polimi.palestraarrampicata.model.Attrezzatura;
import com.polimi.palestraarrampicata.model.Taglia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagliaRepo extends CrudRepository<Taglia,Integer> {

    List<Taglia> findByTagliaAttrezzo(String taglia);
    Taglia findByAttrezzoAndTagliaAttrezzo(Attrezzatura attrezzo, String tagliaAttrezzo);

}
