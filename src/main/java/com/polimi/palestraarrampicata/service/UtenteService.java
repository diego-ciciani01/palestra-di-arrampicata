package com.polimi.palestraarrampicata.service;


import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.request.RequestValutazione;
import com.polimi.palestraarrampicata.dto.response.ResponseCommento;
import com.polimi.palestraarrampicata.dto.response.ResponseLezione;
import com.polimi.palestraarrampicata.exception.CreazioneCommentoFallita;
import com.polimi.palestraarrampicata.exception.InserimentoValutazioneFallita;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.*;
import com.polimi.palestraarrampicata.repository.CommentoRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.repository.ValutazioneRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtenteService implements UserDetailsService {

    private final UtenteRepo utenteRepo;
    private final CommentoRepo commentoRepo;
    private  final ValutazioneRepo valutazioneRepo;
    private  final JwtUtils jwtUtils;
    @Override
    public UserDetails loadUserByUsername(String email) throws IllegalStateException {
        return utenteRepo.findUserByEmail(email).orElseThrow(()-> new IllegalStateException("l'utente non è stato trovato"));
    }

    /**
     * Ottiene la lista degli inviti alle lezioni per l'utente autenticato.
     *
     * @param httpServletRequest HttpServletRequest utilizzato per ottenere l'utente autenticato.
     * @return Lista di inviti alle lezioni per l'utente.
     * @throws EntityNotFoundException Se l'utente autenticato non può essere trovato nel database.
     */
    public List<ResponseLezione> getListInvitiLezione(HttpServletRequest httpServletRequest) throws EntityNotFoundException{

        List<ResponseLezione> lezioneList = new ArrayList<>();

        // Ottiene l'utente autenticato dall'header della richiest
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Verifica se l'utente autenticato ha il ruolo UTENTE, altrimenti lancia un'eccezione
        if(utenteLoggato.getRuolo() != Ruolo.UTENTE)
            throw new RicercaFallita("Utenete inserito non valido");

        // Itera attraverso gli inviti alle lezioni dell'utente e crea la lista di inviti per la risposta
        for(Lezione l: utenteLoggato.getInviti()){
            lezioneList.add(ResponseLezione
                    .builder()
                    .id(l.getId().toString())
                    .dataLezione(l.getData().toString())
                    .statoLezione(l.getStatoLezione())
                    .istruttore(l.getIstruttore().getEmail().toString())
                    .build());
        }
        return lezioneList;
    }

    /**
     * Ottiene la lista degli inviti alle lezioni accettati per l'utente autentificato.
     *
     * @param httpServletRequest HttpServletRequest utilizzato per ottenere l'utente autenticato.
     * @return Lista di inviti alle lezioni accettati per l'utente.
     * @throws EntityNotFoundException Se l'utente autenticato non può essere trovato nel database.
     */
    public List<ResponseLezione> getListInvitiLezioneAccettate(HttpServletRequest httpServletRequest) throws EntityNotFoundException {
        List<ResponseLezione> lezioneList = new ArrayList<>();

        // Ottiene l'utente autenticato dall'header della richiesta
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Verifica se l'utente autenticato ha il ruolo UTENTE, altrimenti lancia un'eccezione
        if(utenteLoggato.getRuolo() != Ruolo.UTENTE)
            throw new RicercaFallita("Utenete inserito non valido");

        // Itera attraverso gli inviti alle lezioni dell'utente e aggiunge solo quelli accettati alla lista di inviti accettati
        for(Lezione l: utenteLoggato.getInviti()){
            if(l.getStatoLezione()) {
                lezioneList.add(ResponseLezione
                        .builder()
                        .id(l.getId().toString())
                        .dataLezione(l.getData().toString())
                        .statoLezione(l.getStatoLezione())
                        .istruttore(l.getIstruttore().getEmail().toString())
                        .build());
            }
        }
        return lezioneList;
    }

    /**
     * Elimina un utente dal sistema in base all'indirizzo email fornito.
     *
     * @param email L'indirizzo email dell'utente da eliminare.
     * @return Messaggio di conferma sull'eliminazione dell'utente.
     */
    public String deleteUserByEmail(String email){
        // Verifica se l'indirizzo email è vuoto, in caso sollev una IllegalStateException
        if(email.isEmpty()) throw new IllegalStateException("Email non esistente");

            // Carica l'utente dal database in base all'indirizzo email
            Utente user = (Utente) loadUserByUsername(email);

            // Elimina l'utente dal database
            utenteRepo.delete(user);

            //Restituisce un messaggio di conferma sull'eliminazione dell'utente
            return "L'utente" + user.getEmail() + "è stato eliminato correttamente";
    }

    /**
     * Crea un nuovo commento associato a un istruttore.
     *
     * @param httpServletRequest La richiesta HTTP.
     * @param requestCommento    I dati del commento da creare.
     * @return Il commento appena creato.
     */
    public Commento creaCommento(HttpServletRequest httpServletRequest, RequestCommento requestCommento){
        try{
            // Ottieni l'utente loggato dalla richiesta HTTP
            Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest,utenteRepo ,jwtUtils );
            // Ottieni la data e ora attuali senza i secondi
            LocalDateTime dataPubblicazione = Utils.withoutSeconds(LocalDateTime.now());
            // Trova l'istruttore commentato in base all'email fornita, se non trovato solleva una eccezione personalizzata CreazioneCommentoFallita
            Utente  istruttore =  utenteRepo.findByEmail(requestCommento.getEmailIstruttoreCommentato())
                    .orElseThrow(() -> new CreazioneCommentoFallita("L'istruttore commentato non esiste"));

            // Ottieni l'ID dell'istruttore commentato
            Integer idIstruttoreCommentato = istruttore.getId();
            Commento commentoNuovo = new Commento();

            Commento commentoPadre = null;
            Integer idCommentoPadre = null;

            // Verifica se è stato fornito un commento padr
            if(requestCommento.getIdCommentoPadre() != null) {
                idCommentoPadre = Integer.parseInt(requestCommento.getIdCommentoPadre());

                if (idCommentoPadre != null)
                    // Trova il commento padre in base all'ID fornito, se non trovato solleva una eccezione personalizzata CreazioneCommentoFallita
                    commentoPadre = commentoRepo.findById(idCommentoPadre)
                            .orElseThrow(() -> new CreazioneCommentoFallita("il commento padre con l'id fornito non esiste"));

                // Verifica se l'istruttore commentato nel commento padre coincide con l'istruttore attuale
                if (!commentoPadre.getIstruttoreCommentato().getId().equals(istruttore.getId()))
                    throw new CreazioneCommentoFallita("L'istruttore commentato a quella del commento padre");
            }

            // Imposta il commento padre (se presente) e altri dettagli del commento
            commentoNuovo.setCommentoPadre(commentoPadre);
            commentoNuovo.setCommentatore(utenteLoggato);
            commentoNuovo.setIstruttoreCommentato(istruttore);
            commentoNuovo.setTesto(requestCommento.getTesto());
            commentoNuovo.setDataInserimento(dataPubblicazione);

            // Salva il commento nel database
            commentoRepo.save(commentoNuovo);

            return commentoNuovo;
        }catch (DateTimeParseException e){
            throw new CreazioneCommentoFallita("Formato data inserimento non valida");
        }catch (IllegalArgumentException e){
            throw new CreazioneCommentoFallita("Parametri errati");
        }
    }

    /**
     * Crea una nuova valutazione per un istruttore.
     *
     * @param httpServletRequest La richiesta HTTP.
     * @param requestValutazione I dati della valutazione da creare.
     * @return La valutazione appena creata.
     */
    public String creaValutazione(HttpServletRequest httpServletRequest, RequestValutazione requestValutazione){
        // Ottieni l'utente loggato dalla richiesta HTTP
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest,utenteRepo ,jwtUtils );

        // Trova l'istruttore da valutare in base all'email fornita
        Utente istruttoreDaValutare=  utenteRepo.findByEmail(requestValutazione.getEmailValutato()).orElse(null);

        if(istruttoreDaValutare == null){
            throw new InserimentoValutazioneFallita("Utente da valutare inserito non esistente");
        }else {
            Valutazione valutazione = valutazioneRepo.findByValutatoreAndValutato(utenteLoggato, istruttoreDaValutare);
            if(valutazione == null){
                Valutazione nuovaValutazione = new Valutazione();
                nuovaValutazione.setValore(Integer.parseInt(requestValutazione.getValore()));
                nuovaValutazione.setValutatore(utenteLoggato);
                nuovaValutazione.setValutato(istruttoreDaValutare);
                valutazioneRepo.save(nuovaValutazione);
                return "valutazione inserita correttamente";
            }else{
                throw new InserimentoValutazioneFallita("la valutazione è già stata inserita per questo istruttore");
            }
        }
    }

    /**
     * Con questo metodo andiamo a prendere una lista di commenti sotto l'id istruttore passato nel
     * metodo, se l'utente loggato ha partecipato alla conversazione dei commenti, ritorniamo la conversazione
     * @param httpServletRequest La richiesta HTTP.
     * @return La valutazione appena creata.
     */
    public List <ResponseCommento> getListCommentifromUtenteToIstruttore(HttpServletRequest httpServletRequest, Integer idIstruttore){
        // Ottieni l'utente loggato dalla richiesta HTTP
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Trova l'istruttore da valutare in base all'email fornita, se non trovato sollevo una EntityNotoFoundException
        Utente istruttore = utenteRepo.findById(idIstruttore).orElseThrow(() -> new EntityNotFoundException("l'istruttore inserito non esiste"));
        List<Commento> commentiUtente = commentoRepo.findAllByCommentatoreAndIstruttoreCommentato(utenteLoggato, istruttore);

        //controllo se è presente del contenuto nei commenti dell'utente
        if(commentiUtente==null || commentiUtente.isEmpty())
            throw new EntityNotFoundException("dall'utente loggato non ci sono commenti fatti verso l'istuttore " + istruttore.getEmail());

        List<ResponseCommento> responseCommenti = new ArrayList<>();
        for(Commento c: commentiUtente){
            responseCommenti.add(ResponseCommento.builder()
                            .emailIstruttore(istruttore.getEmail())
                            .testo(c.getTesto())
                            .commentatore(c.getCommentatore().getEmail())
                            .build());
        }
        return responseCommenti;

    }

    /**
     * Calcola la media delle valutazioni ricevute da un istruttore.
     *
     * @param email L'email dell'istruttore di cui calcolare la media delle valutazioni.
     * @return Il messaggio contenente la media delle valutazioni dell'istruttore.
     * @throws EntityNotFoundException Se l'istruttore corrispondente all'email fornita non esiste.
     */
    public String getAvgValutazione(String email){
        // Trova l'istruttore corrispondente all'email fornita
        Utente istruttore = utenteRepo.findUserByEmailAndRuolo(email, Ruolo.ISTRUTTORE)
                .orElseThrow(() -> new EntityNotFoundException("l'istruttore inserito non esiste"));
        // Trova tutte le valutazioni ricevute dall'istruttore
        List<Valutazione> valutazioniIstruttore = valutazioneRepo.findAllByValutato(istruttore);


        // Se non ci sono valutazioni, restituisci un messaggio appropriato
        if (valutazioniIstruttore.isEmpty()) {
            return "Nessuna valutazione disponibile per l'istruttore " + istruttore.getEmail();
        }
        // Calcola la somma di tutte le valutazioni
        int sumValutazioni = valutazioniIstruttore.stream()
                .mapToInt(Valutazione::getValore)
                .sum();

        // Calcola la media delle valutazioni
        float avg = (float) sumValutazioni / valutazioniIstruttore.size();

        // Restituisci il messaggio contenente la media delle valutazioni dell'istruttore
        return "la media delle valutazione dell'istruttore "+ istruttore.getEmail() +" è "+ avg;

    }



}
