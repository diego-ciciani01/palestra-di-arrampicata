package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestPalestra;
import com.polimi.palestraarrampicata.dto.response.ResponsePalestra;
import com.polimi.palestraarrampicata.model.Palestra;
import com.polimi.palestraarrampicata.repository.PalestraRepo;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
@AllArgsConstructor
public class PalestraService {

    private final PalestraRepo palestraRepo;

    public Palestra createPalestra(RequestPalestra requestPalestra) throws EntityNotFoundException{
        //Optional<Palestra> palestra = palestraRepo.findByNome(requestPalestra.getNomePalestra());
        Optional<Palestra> palestra = palestraRepo.findByNomeAndIndirizzo(requestPalestra.getNomePalestra(), requestPalestra.getIndirizzo());
        // controllo se la palestra che si vuole creare è gia presente o meno
        if(palestra.isPresent()) throw new IllegalStateException("Palestra già presente");
        if(requestPalestra.getCap().length() != 5) throw new IllegalStateException("il cap deve essere di 5 numeri");
        Pattern pattern = Pattern.compile(Utils.REGEX_TELEFONO);
        Matcher matcher = pattern.matcher(requestPalestra.getTelefono());
        if(matcher.matches() == false) throw new IllegalStateException("il numero di telefono inserito non rispetta il pattern dei numeri italiani");

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
