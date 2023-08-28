package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestCorso;
import com.polimi.palestraarrampicata.dto.request.RequestIscriviti;
import com.polimi.palestraarrampicata.dto.response.ResponseCorso;
import com.polimi.palestraarrampicata.exception.CreazioneAttivitaFallita;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.*;
import com.polimi.palestraarrampicata.repository.CorsoRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CorsoService {
    private final CorsoRepo corsoRepo;
    private final UtenteRepo utenteRepo;
    private final JwtUtils jwtUtils;

    public Corso creaCorso(RequestCorso requestCorso) throws EntityNotFoundException {
        LocalDate inizioCorso = Utils.formatterData(requestCorso.getDataDiInizio());
        Utente istruttore = utenteRepo.findUserByEmailAndRuolo(requestCorso.getEmailIstruttore(), Ruolo.ISTRUTTORE)
                .orElseThrow(()-> new EntityNotFoundException("L'istruttore inserito non esiste"));
        // prendi tutti i corsi che fa l'istruttore, se ha già un corso che inizia lo stesso giorno di un'altro
        // bisogna spostare la data di inizio
        for(Corso corso: istruttore.getCorsiTenuti()){
            if(corso.getIstruttoreCorso().getEmail().equals(requestCorso.getEmailIstruttore())){
                if(corso.getDataInizio().equals(requestCorso.getDataDiInizio()))
                    throw new DateTimeException("non è possbile far iniziare 2 corsi allo stesso istruttore nella stessa data");
            }
        }

        Corso corso = new Corso();
        float costoCorso = 0;
        try{
            costoCorso = Float.parseFloat(requestCorso.getCosto());
        }catch (NumberFormatException ex){
            System.out.println("Invalid float format: " + requestCorso.getCosto());
        }
        try{
            Difficolta difficolta = Difficolta.fromString(requestCorso.getDifficolta());
            corso.setDifficolta(difficolta);
        }catch (IllegalArgumentException e) {
            throw new CreazioneAttivitaFallita("La difficoltà inserita non è valida");
        }
        corso.setCosto(costoCorso);
        corso.setIstruttoreCorso(istruttore);
        corso.setSettimaneDiCorso(Integer.parseInt(requestCorso.getNumeroSettimane()));
        corso.setNome(requestCorso.getNomeCorso());
        corso.setDataInizio(inizioCorso);
        if(istruttore.getCorsiTenuti().isEmpty()){
            List<Corso> corsiTenuti = new ArrayList<>();
            corsiTenuti.add(corso);
            istruttore.setCorsiTenuti(corsiTenuti);
        }else{
            istruttore.getCorsiIscritto().add(corso);
        }
        corsoRepo.save(corso);
        utenteRepo.save(istruttore);

        return corso;
    }

    public ResponseCorso eliminaCorso(Integer idCorso)throws EntityNotFoundException{
         Corso corsoDaEliminare =  corsoRepo.findById(idCorso)
                 .orElseThrow(()-> new IllegalStateException("il corso da eliminare non esiste"));

         corsoRepo.delete(corsoDaEliminare);

         return ResponseCorso.builder()
                    .id(corsoDaEliminare.getId().toString())
                    .nome(corsoDaEliminare.getNome())
                    .emailIstruttore(corsoDaEliminare.getIstruttoreCorso().getEmail())
                    .build();

    }

    public List<ResponseCorso> getListCorso() throws EntityNotFoundException{
        Iterable<Corso> corsi = corsoRepo.findAll();
        List<ResponseCorso> corsiList = new ArrayList<>();

        for(Corso c: corsi){
            corsiList.add(ResponseCorso.builder()
                            .id(c.getId().toString())
                            .dataInizio(c.getDataInizio())
                            .numeroSettimane(c.getSettimaneDiCorso())
                            .nome(c.getNome())
                            .emailIstruttore(c.getIstruttoreCorso().getEmail())
                            .build());
        }
        return corsiList;
    }

    public List<ResponseCorso> getLessionByInstructor(Integer idInstructor) {
        Utente istruttore = utenteRepo.findById(idInstructor).orElseThrow(() -> new EntityNotFoundException("Maestro non trovato"));
        List <Corso> corsiIstruttore = corsoRepo.findAllByIstruttoreCorso(istruttore);
        // prende tutti i corsi che non sono ancora iniziati
        List <Corso> corsiAfterNow = corsiIstruttore.stream().filter(cur -> cur.getDataInizio().isAfter(LocalDate.now())).toList();
        List <ResponseCorso> corsoResponse = new ArrayList<>();
        corsiAfterNow.forEach(elem -> {
            corsoResponse.add(ResponseCorso.builder()
                            .id(elem.getId().toString())
                            .dataInizio(elem.getDataInizio())
                            .emailIstruttore(elem.getIstruttoreCorso().getEmail())
                            .nome(elem.getNome())
                            .numeroSettimane(elem.getSettimaneDiCorso())
                    .build());
        });
        return corsoResponse;
    }

    public Corso iscrivitiCorso(RequestIscriviti requestIscriviti, HttpServletRequest httpServletRequest) throws EntityNotFoundException{
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        if(utenteLoggato.getRuolo() != Ruolo.UTENTE)
            throw new RicercaFallita("solo l'utente può iscriversi ad un corso");

        Corso corso = corsoRepo.findById(requestIscriviti.getId()).orElse(null);
        if(corso==null)
            throw new RicercaFallita("il corso cercato non esiste");

        if(corso.getIscritti().isEmpty()){
            List<Utente> iscritti = new ArrayList<>();
            iscritti.add(utenteLoggato);
            corso.setIscritti(iscritti);
        }else {
            corso.getIscritti().add(utenteLoggato);
        }

        if(utenteLoggato.getCorsiIscritto().isEmpty()){
            List<Corso> corsi = new ArrayList<>();
            corsi.add(corso);
            utenteLoggato.setCorsiIscritto(corsi);
        }else {
            utenteLoggato.getCorsiIscritto().add(corso);
        }

        corsoRepo.save(corso);
        utenteRepo.save(utenteLoggato);

        return corso;

    }

}
