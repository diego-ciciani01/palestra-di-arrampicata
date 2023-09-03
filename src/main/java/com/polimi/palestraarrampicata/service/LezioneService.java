package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestAccettaRiffiuta;
import com.polimi.palestraarrampicata.dto.request.RequestLezione;
import com.polimi.palestraarrampicata.dto.response.ResponseLezione;
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

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LezioneService {
    private  final UtenteRepo utenteRepo;
    private  final LezioneRepo lezioneRepo;
    private final JwtUtils jwtUtils;


    /**
     * con questom metodo l'utente va a creare una lezione, inserendo quale istruttore desidera, lo stato della lezione quando viene creato è sempre in false, deve
     * essere accettata dal maestro
     * @param request Richiesta contenente i dettagli della lezione da creare.
     * @param httpServletRequest La richiesta HTTP corrente.
     * @return La lezione appena creata.
     * @throws EntityNotFoundException Se l'istruttore specificato nella richiesta non esiste.
     * @throws DateTimeException Se la data di inizio lezione è nel passato.
     * @throws IllegalStateException Se l'istruttore è già occupato nelle date della lezione.
     */
    public Lezione createLesson(RequestLezione request, HttpServletRequest httpServletRequest) throws EntityNotFoundException, DateTimeException{
        // fortto la data string passata nella richiesta
        LocalDateTime startLesson = Utils.formatterDataTime(request.getStartLesson());

        // Ottiene l'utente loggato dalla richiesta HTTP
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Trova l'istruttore specificato nella richiesta, sellavo una eccezione nel caso di istruttore non trovato
        Utente istruttore = utenteRepo.findUserByEmailAndRuolo(request.getInstructorEmail(), Ruolo.ISTRUTTORE)
                .orElseThrow(()->new EntityNotFoundException("L'istruttore inserito non esiste"));

        // Controlla se l'istruttore è già occupato nelle date della lezione
        List<Lezione> list  = lezioneRepo.findAllByIstruttore(istruttore);

        if(!dataIsValid(list, startLesson)) throw  new IllegalStateException("Maestro occupato in queste date");

        float durata = 0;

        Lezione lezione = new Lezione();

        // qui faccio il cast da string in tipo FLoat della durata
        try{
            durata  = Float.parseFloat(request.getDuration());
        }catch (NumberFormatException e) {
            System.out.println("Invalid float format: " + request.getDuration());
        }
        // Converti la stringa tipologia lezione in un'enumerazione
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

        // Aggiunta dell'utente loggato agli invitati e dell'invito all'utente loggato
        List<Lezione> inviti = utenteLoggato.getInviti();
        List<Utente> utententiInvitati = lezione.getUtenteMittente();
        if(inviti == null) {
            inviti = new ArrayList<>();
            inviti.add(lezione);
            utenteLoggato.setInviti(inviti);
        }else {
            inviti.add(lezione);
            utenteLoggato.setInviti(inviti);
        }
        if(utententiInvitati == null) {
            utententiInvitati = new ArrayList<>();
            utententiInvitati.add(utenteLoggato);
            lezione.setUtenteMittente(utententiInvitati);
        }else {
            utententiInvitati.add(utenteLoggato);
            lezione.setUtenteMittente(utententiInvitati);
        }


        // Salvataggio dell'entità lezione e dell'utente
        lezioneRepo.save(lezione);
        utenteRepo.save(utenteLoggato);
        return lezione;

    }
    // metodo di supporto per controllare la validita delle date, in modo di non creare conflitti con le altre lezioni
    public boolean dataIsValid(List<Lezione> list, LocalDateTime inizio){
        for (Lezione elm: list){
            if(inizio.isEqual(elm.getData()))
                return  false;
        }
        return true;
    }

    /**
     * Metodo per accettare o rifiutare una richiesta di lezione da parte di un istruttore.
     * @param request La richiesta di accettazione/rifiuto della lezione.
     * @param httpServletRequest La richiesta HTTP in corso.
     * @return L'oggetto Lezione dopo averlo aggiornato.
     * @throws EntityNotFoundException Se l'istruttore non è trovato o l'ID della lezione non corrisponde a nessuna lezione.
     */

    public Lezione accettaRifiutaLezione(RequestAccettaRiffiuta request, HttpServletRequest httpServletRequest) throws  EntityNotFoundException{
        // Ottieni l'utente istruttore loggato dalla richiesta HTTP
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Verifica che l'utente sia un istruttor
        if(utenteLoggato.getRuolo() != Ruolo.ISTRUTTORE)
            throw new RicercaFallita("solo l'istruttore può accettare o meno le richieste di lezione");

        // Scorrere le lezioni per trovare quella corrispondente all'ID fornito nella richiesta
        List<Lezione> lezioni = lezioneRepo.findAllByIstruttore(utenteLoggato);
        for(int i=0; i<lezioni.size(); i++){
            if(lezioni.get(i).getId() == Integer.parseInt(request.getIdLezione())) {
                Lezione lezione = lezioni.get(i);

                // Controlla se la richiesta è per accettare o rifiutare la lezione
                if(Boolean.valueOf(request.getAccetta())){
                    // Imposta lo stato della lezione come accettato
                    lezione.setStatoLezione(Boolean.valueOf(request.getAccetta()));
                }
                // Aggiorna il commento associato alla lezione
                lezione.setCommento(request.getCommento());

                // Salva la lezione aggiornata nel repository
                lezioneRepo.save(lezione);

                // Restituisci la lezione aggiornata
                return lezione;
            }
        }
        // Se non viene trovata una corrispondenza, l'istruttore può accettare solo inviti a lezioni specifiche
        throw new RicercaFallita("l'istruttore può accettare solo gli inviti che gli sono assegnati");


    }

    /**
     * Recupera una lista di oggetti ResponseLezione contenenti informazioni sulle lezioni.
     * @return Una lista di oggetti ResponseLezione.
     * @throws EntityNotFoundException Se non vengono trovate lezioni.
     */
    public List<ResponseLezione> getListLession() throws EntityNotFoundException{
        // Recupera tutte le lezioni dal repository
        Iterable<Lezione> lezioni = lezioneRepo.findAll();

        // Lista in cui verranno memorizzate le informazioni sulle lezioni
        List<ResponseLezione> lessonList = new ArrayList<>();

        // Scorrere tutte le lezioni e creare oggetti ResponseLezione corrispondenti
        for(Lezione l : lezioni){
                    lessonList.add(ResponseLezione.builder()
                            .id(l.getId().toString())
                            .dataLezione(l.getData().toString())
                            .statoLezione(l.getStatoLezione())
                            .istruttore(l.getIstruttore().getEmail())
                            .iscritti(l.getUtenteMittente())
                            .build());

        }
        return lessonList;
    }

    /**
     * Recupera una lista di oggetti ResponseLezione che corrispondono alle lezioni
     * tenute da un istruttore specifico.
     * @param idInstructor L'ID dell'istruttore di cui si vogliono ottenere le lezioni.
     * @return Una lista di oggetti ResponseLezione corrispondenti alle lezioni dell'istruttore.
     * @throws EntityNotFoundException Se l'istruttore non viene trovato.
     */
    public List<ResponseLezione> getLessionByInstructor(Integer idInstructor) {
        // Recupera l'istruttore dal repository degli utenti
        Utente istruttore = utenteRepo.findById(idInstructor).orElseThrow(() -> new EntityNotFoundException("Maestro non trovato"));

        // Recupera tutte le lezioni tenute dall'istruttore
        List <Lezione> lezioniInstructor = lezioneRepo.findAllByIstruttore(istruttore);

        // Filtra le lezioni che avvengono dopo l'orario attuale
        List <Lezione> lessionAfterNow = lezioniInstructor.stream().filter(cur -> cur.getData().isAfter(LocalDateTime.now())).toList();

        // Lista in cui verranno memorizzate le informazioni sulle lezioni
        List <ResponseLezione> lessonsResponse = new ArrayList<>();

        // Creazione degli oggetti ResponseLezione basati sulle lezioni filtrate
        lessionAfterNow.forEach(elem -> {
            lessonsResponse.add(ResponseLezione.builder()
                            .id(elem.getId().toString())
                            .dataLezione(elem.getData().toString())
                            .iscritti(elem.getUtenteMittente())
                            .statoLezione(elem.getStatoLezione())
                            .build());
        });
        return lessonsResponse;
    }
}
