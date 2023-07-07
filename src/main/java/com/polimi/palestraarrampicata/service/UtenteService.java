package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestRegistrazione;
import com.polimi.palestraarrampicata.exception.RegistrazioneFallita;
import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class UtenteService {
    @Autowired
    private UtenteRepo utenteRepo;

    public Utente findByUsername(String username) {
        return utenteRepo.findByUsername(username);
    }

    public Utente registrazione(RequestRegistrazione requestRegistrazione){
        Utente utente = new Utente();

        try{
            String username = requestRegistrazione.getUsername();
            String password = requestRegistrazione.getPassword();

            if (utenteRepo.findByUsernameOrEmail(username, password) != null){
                throw new RegistrazioneFallita("Username o Email già in uso");
            }
            utente.setNome(requestRegistrazione.getNome());
            utente.setCognome(requestRegistrazione.getCognome());
            utente.setUsername(requestRegistrazione.getUsername());
            utente.setEmail(requestRegistrazione.getEmail());
            utente.setPassword(requestRegistrazione.getPassword());
            Ruolo ruolo = Ruolo.valueOf(requestRegistrazione.getRuolo().toUpperCase());
            utente.setRuolo(ruolo);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime dataNascita;
            try{
                System.out.println(requestRegistrazione.getDataNascita());
                dataNascita = LocalDateTime.parse(requestRegistrazione.getDataNascita(), formatter);
            }catch (DateTimeException e){
                throw new RegistrazioneFallita("Formato data di nascita non valido");
            }

            utente.setDataDiNascita(dataNascita);
            String fotoProfilo = requestRegistrazione.getFotoProfilo();

            if(fotoProfilo != null){
                if(fotoProfilo.isBlank()) {
                    throw new RegistrazioneFallita("La foto profilo, se inserita, non può essere vuota");
                }
                byte[] fotoProfiloBytes = Base64.getDecoder().decode(fotoProfilo.getBytes(StandardCharsets.UTF_8));
                utente.setFotoProfilo(fotoProfiloBytes);
            }
            utenteRepo.save(utente);

        }catch (IllegalArgumentException e){
            throw  new RegistrazioneFallita("il ruolo inserito non è valido");
        }
        return  utente;
    }


}
