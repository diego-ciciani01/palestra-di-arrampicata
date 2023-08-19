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
    /*
    @Query("SELECT SUM(ta.quantita)  FROM taglia ta  WHERE ta.attrezzo = ?1")
    Integer calcolaSommaQuantita(Integer id)    */

    /*
    @Query("SELECT quantita  FROM taglia t  WHERE t.taglia_attrezzo= :valore")
    Integer cercaQuantitaPerTipologiaDiTaglia(@Param("valore") String valore);
*/
   Optional <Taglia> findByIdAndTagliaAttrezzo(Integer id, String tagliaAttrezzo);


}
