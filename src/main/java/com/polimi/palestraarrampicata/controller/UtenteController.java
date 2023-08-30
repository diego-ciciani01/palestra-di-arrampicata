package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.request.RequestValutazione;
import com.polimi.palestraarrampicata.dto.response.ResponseCommento;
import com.polimi.palestraarrampicata.service.UtenteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UtenteController {

    private final UtenteService utenteService;

    /**
     * Questo EndPoint permette di ottenere la lista di tutti gli inviti di lezioni private fatte
     * all'istruttore che sono state accettate dall'istruttore
     * @param request Oggetto HttpServletRequest contenente le informazioni sulla richiesta HTTP.
     * @return ResponseEntity contenente la lista delle lezioni accettate dall'utente.
     * @throws EntityNotFoundException Se non ci sono lezioni disponibili o l'utente non viene trovato.
     */
    @GetMapping("/getAll/inviti/accettati")
    public ResponseEntity<?> getAllLessonsAccettate(HttpServletRequest request){
        try{
            return ResponseEntity.ok(utenteService.getListInvitiLezioneAccettate(request));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * il seguente metodo ritorna una lista di tutte le richiste mandate dall'utente loggato ma che ancora non
     * sono state approvate dall'istruttore
     * @param request Oggetto HttpServletRequest contenente le informazioni sulla richiesta HTTP.
     * @return ResponseEntity contenente la lista degli inviti alle lezioni dell'utente.
     * @throws EntityNotFoundException Se non ci sono lezioni disponibili o l'utente non viene trovato.
     */
    @GetMapping("/getAllInviti")
    public ResponseEntity<?> getAllLessons(HttpServletRequest request){
        try{
            return ResponseEntity.ok(utenteService.getListInvitiLezione(request));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Elimina un utente dal sistema in base all'indirizzo email.
     * Solo L'ADMIN può eseguire questa operazione
     * @param email Indirizzo email dell'utente da eliminare.
     * @return ResponseEntity contenente un messaggio di conferma dell'eliminazione.
     * @throws IllegalStateException Se si verifica un errore durante l'eliminazione dell'utente.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/email/{email}")
    public ResponseEntity<String> deleteUtente(@PathVariable("email") String email){
        try{
            return ResponseEntity.ok(utenteService.deleteUserByEmail(email));
        }catch (IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crea un nuovo commento da parte di un utente su un istruttore.
     *
     * @param requestCommento Oggetto contenente i dettagli del commento da creare.
     * @param request HttpServletRequest utilizzato per ottenere l'utente autenticato.
     * @return ResponseEntity contenente il commento creato come risposta.
     * @throws IllegalStateException Se si verifica un errore durante la creazione del commento.
     * @throws EntityNotFoundException Se l'entità (utente o istruttore) non è trovata nel sistema.
     */
    @PostMapping("/commenta/istruttore")
    public ResponseEntity<?> creaCommento(@Valid @RequestBody RequestCommento requestCommento, HttpServletRequest request){
        try{
             ResponseCommento responseCommento =  DTOManager.ToResponseCommentoBYCommento(utenteService.creaCommento(request, requestCommento));
            return ResponseEntity.ok(responseCommento);
        }catch (IllegalStateException | EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Crea una nuova valutazione da parte di un utente su un istruttore.
     *
     * @param requestValutazione Oggetto contenente i dettagli della valutazione da creare.
     * @param result BindingResult per la validazione dei dati di input.
     * @param request HttpServletRequest utilizzato per ottenere l'utente autenticato.
     * @return ResponseEntity contenente il risultato dell'operazione di creazione della valutazione.
     */
    @PreAuthorize("hasAuthority('UTENTE')")
    @PostMapping("valuta/istruttore")
    public ResponseEntity<?> creaValutazione(@Valid @RequestBody RequestValutazione requestValutazione, BindingResult result, HttpServletRequest request){
        try{
            return ResponseEntity.ok(utenteService.creaValutazione(request, requestValutazione));
        }catch (IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Ottiene tutti i commenti lasciati dagli utenti su un determinato istruttore.
     *
     * @param idIstruttore L'ID dell'istruttore per il quale si desidera ottenere i commenti.
     * @param httpServletRequest HttpServletRequest utilizzato per ottenere l'utente autenticato.
     * @return ResponseEntity contenente la lista dei commenti degli utenti sull'istruttore.
     */
    @GetMapping("commenti/getAllByIstruttore/{id_istruttore}")
    public ResponseEntity<?> getAllCommenti(@PathVariable("id_istruttore") Integer idIstruttore, HttpServletRequest httpServletRequest){
        try {
            return ResponseEntity.ok(utenteService.getListCommentifromUtenteToIstruttore(httpServletRequest, idIstruttore));

        }catch (IllegalStateException | EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Ottiene la media delle valutazioni per un istruttore specificato per email.
     *
     * @param email L'indirizzo email dell'istruttore di cui si vuole ottenere la media delle valutazioni.
     * @return La media delle valutazioni dell'istruttore specificato.
     */
    @PostMapping("valuta/getAvg/{email_istruttore}")
    public ResponseEntity<?> getAvgValutazioniByIstruttore(@PathVariable("email_istruttore") String email) {
        try {
            return ResponseEntity.ok(utenteService.getAvgValutazione(email));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



}
