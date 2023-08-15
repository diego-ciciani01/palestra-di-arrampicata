package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestPalestra;
import com.polimi.palestraarrampicata.dto.response.ResponsePalestra;
import com.polimi.palestraarrampicata.model.Palestra;
import com.polimi.palestraarrampicata.repository.PalestraRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PalestraService {

    private final PalestraRepo palestraRepo;

    public Palestra createPalestra(RequestPalestra requestPalestra) throws EntityNotFoundException{
        //Optional<Palestra> palestra = palestraRepo.findByNome(requestPalestra.getNomePalestra());
        Optional<Palestra> palestra = palestraRepo.findByNomeAndIndirizzo(requestPalestra.getNomePalestra(), requestPalestra.getIndirizzo());
        // controllo se la palestra che si vuole creare è gia presente o meno
        if(palestra.isPresent()) throw new IllegalStateException("Palestra già presente");
        Palestra newPalestra = new Palestra(
                requestPalestra.getCap(),
                requestPalestra.getCitta(),
                requestPalestra.getTelefono(),
                requestPalestra.getIndirizzo(),
                requestPalestra.getNomePalestra()
        );
        palestraRepo.save(newPalestra);
        return newPalestra;
    }

    public List<ResponsePalestra> getAllPalestre(){
        Iterable<Palestra> palestre = palestraRepo.findAll();
        List<ResponsePalestra> palestreResponse = new ArrayList<>();

        for(Palestra p: palestre){
            palestreResponse.add(ResponsePalestra.builder()
                            .nome(p.getNome())
                            .id(p.getId().toString())
                            .build());
        }
        return palestreResponse;
    }

}
