package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestCorso;
import com.polimi.palestraarrampicata.dto.request.RequestIscriviti;
import com.polimi.palestraarrampicata.dto.response.ResponseCorso;
import com.polimi.palestraarrampicata.exception.CreazioneAttivitaFallita;
import com.polimi.palestraarrampicata.exception.RegistrazioneFallita;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.*;
import com.polimi.palestraarrampicata.repository.CorsoRepo;
import com.polimi.palestraarrampicata.repository.PalestraRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.strategy.ContextListaCorsi;
import com.polimi.palestraarrampicata.strategy.ListaCorsi;
import com.polimi.palestraarrampicata.strategy.ListaCorsiIstruttore;
import com.polimi.palestraarrampicata.strategy.ListaCorsiPerDifficolta;
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
    private final PalestraRepo palestraRepo;
    private final JwtUtils jwtUtils;


    /**
     * Crea un nuovo corso nel sistema.
     *
     * @param requestCorso Oggetto contenente i dettagli del corso da creare.
     * @return L'oggetto Corso appena creato.
     * @throws EntityNotFoundException Se l'istruttore specificato non esiste o se non viene trovato un utente istruttore.
     * @throws CreazioneAttivitaFallita Se la difficoltà specificata non è valida.
     * @throws DateTimeException Se ci sono conflitti di date tra i corsi dello stesso istruttore.
     */
    public Corso creaCorso(RequestCorso requestCorso) throws EntityNotFoundException {
        // Converte la data di inizio del corso.
        LocalDate inizioCorso = Utils.formatterData(requestCorso.getDataDiInizio());

        // Trova l'istruttore corrispondente all'email specificata dalla repository utenteRepo.
        Utente istruttore = utenteRepo.findUserByEmailAndRuolo(requestCorso.getEmailIstruttore(), Ruolo.ISTRUTTORE)
                .orElseThrow(()-> new EntityNotFoundException("L'istruttore inserito non esiste"));

        // cerco la palestra che corrisponde alla email passata nella request
        Palestra palestra = palestraRepo.findByEmailPalestra(requestCorso.getEmailPalestraCorso())
                .orElseThrow(() -> new EntityNotFoundException("la palestra cerca non esiste"));


        // prendi tutti i corsi che fa l'istruttore, se ha già un corso che inizia lo stesso giorno di un'altro
        // bisogna spostare la data di inizio, evitiamo i conflitti con le date
        for(Corso corso: istruttore.getCorsiTenuti()){
            if(corso.getIstruttoreCorso().getEmail().equals(requestCorso.getEmailIstruttore())){
                if(corso.getDataInizio().equals(requestCorso.getDataDiInizio()))
                    throw new DateTimeException("non è possbile far iniziare 2 corsi allo stesso istruttore nella stessa data");
            }
        }

        Corso corso = new Corso();
        float costoCorso = 0;

        // Converte la stringa del costo in un float.
        try{
            costoCorso = Float.parseFloat(requestCorso.getCosto());
        }catch (NumberFormatException ex){
            System.out.println("Invalid float format: " + requestCorso.getCosto());
        }
        try{
            // Converte la stringa della difficoltà in un enum Difficolta
            Difficolta difficolta = Difficolta.fromString(requestCorso.getDifficolta());
            corso.setDifficolta(difficolta);
        }catch (IllegalArgumentException e) {
            throw new CreazioneAttivitaFallita("La difficoltà inserita non è valida");
        }

        // impostiamo i dati nell'oggetto
        corso.setCosto(costoCorso);
        corso.setIstruttoreCorso(istruttore);
        corso.setCorsoPalestra(palestra);
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

        if(palestra.getCorsiPalestra().isEmpty()){
            List<Corso> corsiPalestra = new ArrayList<>();
            corsiPalestra.add(corso);
            palestra.setCorsiPalestra(corsiPalestra);
        }else {
            palestra.getCorsiPalestra().add(corso);
        }

        // Salvataggio del corso e dell'istruttore.
        corsoRepo.save(corso);
        utenteRepo.save(istruttore);
        palestraRepo.save(palestra);

        return corso;
    }

    /**
     * Elimina un corso dal sistema.
     *
     * @param idCorso ID del corso da eliminare.
     * @return Oggetto ResponseCorso contenente i dettagli del corso eliminato.
     * @throws EntityNotFoundException Se il corso da eliminare non viene trovato.
     */
    public ResponseCorso eliminaCorso(Integer idCorso)throws EntityNotFoundException{
        // Trova il corso da eliminare tramite l'ID specificato dalla repository corsoRepo.
         Corso corsoDaEliminare =  corsoRepo.findById(idCorso)
                 .orElseThrow(()-> new IllegalStateException("il corso da eliminare non esiste"));

         // Elimina il corso dalla repository corsoRepo.
         corsoRepo.delete(corsoDaEliminare);

        // Crea un oggetto ResponseCorso con i dettagli del corso eliminato e lo restituisce.
         return ResponseCorso.builder()
                    .id(corsoDaEliminare.getId().toString())
                    .nome(corsoDaEliminare.getNome())
                    .emailIstruttore(corsoDaEliminare.getIstruttoreCorso().getEmail())
                    .build();

    }

    /**
     * Ottiene una lista di tutti i corsi disponibili nel sistema.
     *
     * @return Lista di oggetti ResponseCorso contenenti i dettagli dei corsi.
     * @throws EntityNotFoundException Se non vengono trovati corsi nel sistema.
     */
    public List<ResponseCorso> getListCorso() throws EntityNotFoundException{
        // Ottiene tutti i corsi dalla repository corsoRepo.
        Iterable<Corso> corsi = corsoRepo.findAll();
        List<ResponseCorso> corsiList = new ArrayList<>();


        // Converte ciascun corso in un oggetto ResponseCorso e lo aggiunge alla lista.
        for(Corso c: corsi){
            corsiList.add(ResponseCorso.builder()
                            .id(c.getId().toString())
                            .dataInizio(c.getDataInizio())
                            .numeroSettimane(c.getSettimaneDiCorso())
                            .nome(c.getNome())
                            .emailIstruttore(c.getIstruttoreCorso().getEmail())
                            .build());
        }
        // Restituisce la lista dei corsi.
        return corsiList;
    }

    /**
     * Ottiene una lista dei corsi tenuti da un istruttore specifico, che non sono ancora iniziati.
     *
     * @param idInstructor ID dell'istruttore di cui ottenere i corsi.
     * @return Lista di oggetti ResponseCorso contenenti i dettagli dei corsi dell'istruttore.
     * @throws EntityNotFoundException Se l'istruttore specificato non viene trovato.
     */
    public List<ResponseCorso> getLessionByInstructor(Integer idInstructor) {
        // Trova l'istruttore corrispondente all'ID specificato dalla repository utenteRepo.
        Utente istruttore = utenteRepo.findById(idInstructor).orElseThrow(() -> new EntityNotFoundException("Maestro non trovato"));

        //utilizzo del desing pattern strategy, utilizzato per effettuare una ricerca per istruttore, mostrando solamente i corsi che non sono
        // ancora passati
        ContextListaCorsi corsistruttore = new ContextListaCorsi();
        corsistruttore.eseguiStrategiaListaCorso(new ListaCorsiIstruttore());
        List<Corso> corsi =  corsistruttore.eseguiRicerca(istruttore,corsoRepo);
        List <ResponseCorso> corsoResponse = new ArrayList<>();

        // Converte ciascun corso nella lista filtrata in un oggetto ResponseCorso e lo aggiunge alla lista.
        corsi.forEach(elem -> {
            corsoResponse.add(ResponseCorso.builder()
                            .id(elem.getId().toString())
                            .dataInizio(elem.getDataInizio())
                            .emailIstruttore(elem.getIstruttoreCorso().getEmail())
                            .nome(elem.getNome())
                            .numeroSettimane(elem.getSettimaneDiCorso())
                    .build());
        });

        // Restituisce la lista dei corsi dell'istruttore che non sono ancora iniziati.
        return corsoResponse;
    }
    /**
     * Restituisce una lista di corsi in base alla difficoltà specificata.
     *
     * @param difficolta La difficoltà dei corsi da cercare.
     * @return Una lista di corsi che corrispondono alla difficoltà specificata.
     */
    public List<ResponseCorso> getCorsiByDifficolta(String difficolta){
        // Creazione di un contesto per l'uso della strategia
        ContextListaCorsi corsistruttore = new ContextListaCorsi();

        // Esecuzione della strategia per ottenere la lista di corsi per difficoltà
        corsistruttore.eseguiStrategiaListaCorso(new ListaCorsiPerDifficolta());

        // Esecuzione della ricerca dei corsi con la difficoltà specificata
        List<Corso> corsi = corsistruttore.eseguiRicerca(corsoRepo, difficolta);

        List <ResponseCorso> corsoResponse = new ArrayList<>();

        // Trasformazione dei risultati della ricerca in oggetti ResponseCorso
        corsi.forEach(elem -> {
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


    /**
     * Iscrive un utente a un corso nel sistema.
     *
     * @param requestIscriviti Oggetto contenente i dettagli dell'iscrizione al corso.
     * @param httpServletRequest Oggetto HttpServletRequest per ottenere l'utente dalla richiesta.
     * @return L'oggetto Corso a cui l'utente si è iscritto.
     * @throws RicercaFallita Se l'utente non è un utente o se il corso cercato non esiste.
     */
    public Corso iscrivitiCorso(RequestIscriviti requestIscriviti, HttpServletRequest httpServletRequest) throws EntityNotFoundException{
        // Ottiene l'utente loggato dalla richiesta HTTP.
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Controlla se l'utente loggato ha il ruolo di UTENTE.
        if(utenteLoggato.getRuolo() != Ruolo.UTENTE)
            throw new RicercaFallita("solo l'utente può iscriversi ad un corso");

        // Trova il corso a cui l'utente vuole iscriversi tramite l'ID specificato dalla repository corsoRepo.
        Corso corso = corsoRepo.findById(requestIscriviti.getId()).orElse(null);

        // controllo se l'utnte è iscritto nella stessa palestra dove viene tenuto il corso, in casso cotrario sollevo un'eccezione
        if(!corso.getCorsoPalestra().getIscrittiPalestra().contains(utenteLoggato))
            throw new RegistrazioneFallita("l'utente non è registrato nella palestra dove si svolge il corso");

        // Se il corso non viene trovato, solleva un'eccezione.
        if(corso==null)
            throw new RicercaFallita("il corso cercato non esiste");

        // Aggiunge l'utente al corso.
        if(corso.getIscritti().isEmpty()){
            List<Utente> iscritti = new ArrayList<>();
            iscritti.add(utenteLoggato);
            corso.setIscritti(iscritti);
        }else {
            corso.getIscritti().add(utenteLoggato);
        }
        // Aggiunge il corso all'elenco dei corsi a cui l'utente è iscritto
        if(utenteLoggato.getCorsiIscritto().isEmpty()){
            List<Corso> corsi = new ArrayList<>();
            corsi.add(corso);
            utenteLoggato.setCorsiIscritto(corsi);
        }else {
            utenteLoggato.getCorsiIscritto().add(corso);
        }

        // Salva le modifiche al corso e all'utente nell'apposita repository
        corsoRepo.save(corso);
        utenteRepo.save(utenteLoggato);

        // Restituisce il corso a cui l'utente si è iscritto.
        return corso;

    }

}
