package com.polimi.palestraarrampicata.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.polimi.palestraarrampicata.dto.request.RequestAccettaRiffiuta;
import com.polimi.palestraarrampicata.dto.request.RequestLezione;
import com.polimi.palestraarrampicata.dto.response.ResposeLezione;
import com.polimi.palestraarrampicata.dto.response.ResposeUtente;
import com.polimi.palestraarrampicata.exception.CreazioneAttivitaFallita;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.Lezione;
import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.TipologiaLezione;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.LezioneRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriTemplate;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LezioneService {
    private  final UtenteRepo utenteRepo;
    private  final LezioneRepo lezioneRepo;
    private final JwtUtils jwtUtils;


    /**
     * con questom metodo l'utente va a creare una lezione, inserendo quale istruttore desidera, lo stato della lezione quando viene creato è sempre in false, deve
     * essere accettata dal maestro
     * @param request
     * @param httpServletRequest
     * @return
     * @throws EntityNotFoundException
     * @throws DateTimeException
     */
    public Lezione createLesson(RequestLezione request, HttpServletRequest httpServletRequest) throws EntityNotFoundException, DateTimeException{
        LocalDateTime startLesson = Utils.formatterDataTime(request.getStartLesson());
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        Ruolo ruolo = Ruolo.ISTRUTTORE;
        Utente istruttore = utenteRepo.findUserByEmailAndRuolo(request.getInstructorEmail(), ruolo)
                .orElseThrow(()->new EntityNotFoundException("L'istruttore non esiste"));

        List<Lezione> list  = lezioneRepo.findAllByIstruttore(istruttore);
        if(!dataIsValid(list, startLesson)) throw  new IllegalStateException("Maestro occupato in queste date");
        float durata = 0;
        Lezione lezione = new Lezione();
        try{
            durata  = Float.parseFloat(request.getDuration());
        }catch (NumberFormatException e) {
            System.out.println("Invalid float format: " + request.getDuration());
        }

        try{
            TipologiaLezione tipo = TipologiaLezione.fromString(request.getTipologiaLezione());
            lezione.setTipologiaLezione(tipo);
        }catch (IllegalArgumentException e) {
            throw new CreazioneAttivitaFallita("La difficoltà inserita non è valida");
        }
       //creazione lezione
        lezione.setData(startLesson);
        lezione.setDurata(durata);
        lezione.setIstruttore(istruttore);
        lezione.setStatoLezione(false);
        //lezione.getUtentiInvitati().add(utenteLoggato);
        //utenteLoggato.getLezioniIscritte().add(lezione);
        List<Lezione> inviti = utenteLoggato.getInviti();
        List<Utente> utententiInvitati = lezione.getUtentiInvitati();
        if(inviti == null)
            inviti = new ArrayList<>();

        if(utententiInvitati == null)
            utententiInvitati = new ArrayList<>();

        inviti.add(lezione);
        utententiInvitati.add(utenteLoggato);

        utenteLoggato.setInviti(inviti);
        lezione.setUtentiInvitati(utententiInvitati);

        lezioneRepo.save(lezione);
        utenteRepo.save(utenteLoggato);
        return lezione;

    }

    public boolean dataIsValid(List<Lezione> list, LocalDateTime inizio){
        for (Lezione elm: list){
            if(inizio.isEqual(elm.getData()))
                return  false;
        }
        return true;
    }

    public Lezione accettaRifiutaLezione(RequestAccettaRiffiuta request, HttpServletRequest httpServletRequest) throws  EntityNotFoundException{
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        if(utenteLoggato.getRuolo() != Ruolo.ISTRUTTORE)
            throw new RicercaFallita("solo l'istruttore può accettare o meno le richieste di lezione");

        List<Lezione> lezioni = lezioneRepo.findAllByIstruttore(utenteLoggato);
        if(!lezioni.contains(Integer.parseInt(request.getIdLezione())))
            throw new RicercaFallita("l'istruttore può accettare solo gli inviti che gli sono assegnate");

        Lezione lezione = lezioni.get(Integer.parseInt(request.getIdLezione()));
        lezione.setStatoLezione(Boolean.valueOf(request.getAccetta()));
        lezione.setCommento(request.getCommento());
        lezioneRepo.save(lezione);
        return lezione;
    }

    public List<ResposeLezione> getListLession() throws EntityNotFoundException{
        Iterable<Lezione> lezioni = lezioneRepo.findAll();
        List<ResposeLezione> lessonList = new ArrayList<>();

        for(Lezione l : lezioni){
                    lessonList.add(ResposeLezione.builder()
                            .id(l.getId().toString())
                            .dataLezione(l.getData().toString())
                            .statoLezione(l.getStatoLezione())
                            .istruttore(l.getIstruttore().getEmail().toString())
                            .iscritti(l.getUtentiInvitati())
                            .build());

        }
        return lessonList;
    }

    public List<ResposeLezione> getLessionByInstructor(Integer idInstructor) {
        Utente istruttore = utenteRepo.findById(idInstructor).orElseThrow(() -> new EntityNotFoundException("Maestro non trovato"));
        List <Lezione> lezioniInstructor = lezioneRepo.findAllByIstruttore(istruttore);
        List <Lezione> lessionAfterNow = lezioniInstructor.stream().filter(cur -> cur.getData().isAfter(LocalDateTime.now())).toList();
        List <ResposeLezione> lessonsResponse = new ArrayList<>();
        lessionAfterNow.forEach(elem -> {
            lessonsResponse.add(ResposeLezione.builder()
                            .id(elem.getId().toString())
                            .dataLezione(elem.getData().toString())
                            .iscritti(elem.getUtentiInvitati())
                            .statoLezione(elem.getStatoLezione())
                            .build());
        });
        return lessonsResponse;
    }
}