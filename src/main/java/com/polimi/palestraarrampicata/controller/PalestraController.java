package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestIscrivitiPalestra;
import com.polimi.palestraarrampicata.dto.request.RequestPalestra;
import com.polimi.palestraarrampicata.dto.response.ResponsePalestra;
import com.polimi.palestraarrampicata.dto.response.ResponseUtente;
import com.polimi.palestraarrampicata.service.PalestraService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/palestra")
@RequiredArgsConstructor
public class PalestraController {
    private final PalestraService palestraService;

    /**
     * Crea una nuova palestra in base alle informazioni fornite nella richiesta e restituisce una risposta con i dettagli della palestra creata.
     * Questo metodo è accessibile solo agli utenti con il ruolo 'ADMIN'.
     *
     * @param requestPalestra La richiesta contenente i dettagli per creare la nuova palestra.
     * @return Una risposta contenente i dettagli della palestra appena creata.
     * Restituisce una risposta con codice HTTP 200 (OK) e i dettagli della palestra appena creata.
     * nel caso di eccezioni sollevate il metodo ritorna una BadRequest 400
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ResponsePalestra> createPalestra(@Valid @RequestBody RequestPalestra requestPalestra){
        try {
            return ResponseEntity.ok(DTOManager.toPalestraResponseByPalestra(palestraService.createPalestra(requestPalestra)));
        }catch (IllegalStateException enf){
            return new ResponseEntity<>(ResponsePalestra.builder().message(enf.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Ottiene tutte le palestre esistenti e restituisce una risposta con la lista delle palestre.
     *
     * @return Una risposta contenente la lista delle palestre.
     * Restituisce una risposta con codice HTTP 200 (OK) e la lista di tutte le palestre.
     * nel caso di eccezioni sollevate il metodo ritorna una BadRequest 400
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPalestre(){
        try{
            return ResponseEntity.ok(palestraService.getAllPalestre());
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * Ottiene tutti gli utenti iscritti a una palestra specifica e restituisce una risposta con la lista degli utenti.
     *
     * @param palestaEmail L'indirizzo email della palestra per cui si vogliono ottenere gli utenti iscritti.
     * @return Una risposta contenente la lista degli utenti iscritti alla palestra.
     * Restituisce una risposta con codice HTTP 200 (OK).
     * nel caso di eccezione sollevate il metodo ritorna una BadRequest 400
     */
    @GetMapping("/getAll/iscrittiByPalestra/{palestra_email}")
    public ResponseEntity<?> getAllUtentiByPalestra(@PathVariable("palestra_email") String palestaEmail){
        try{
              List<ResponseUtente> responseUtenteList = DTOManager.toUserResponseByUsers(palestraService.getAllIscrittiBYEmailPalestra(palestaEmail));
                return ResponseEntity.ok(responseUtenteList);
        }catch (IllegalStateException enf){
            return new ResponseEntity<>(ResponsePalestra.builder().message(enf.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Disiscrive un utente dalla palestra utilizzando l'indirizzo email dell'utente e restituisce una risposta conferma.
     *
     * @param email_utente L'indirizzo email dell'utente che si desidera disiscrivere dalla palestra.
     * @return Una risposta conferma che indica che l'utente è stato disiscritto con successo dalla palestra.
     * Restituisce una risposta con codice HTTP 200 (OK).
     * Nel caso di eccezione sollevate il metodo ritorna una BadRequest 400
     */
    @PostMapping("/disiscrivi/{email_utente}")
    public ResponseEntity<?> disiscriviUtente(@PathVariable("email_utente") String email_utente){
        try{
            return ResponseEntity.ok(palestraService.disiscriviUtente(email_utente));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * Iscrive un utente alla palestra utilizzando i dati forniti nella richiesta e restituisce una risposta conferma.
     *
     * @param requestIscrivitiPalestra La richiesta contenente i dati per l'iscrizione dell'utente alla palestra.
     * @param httpServletRequest L'oggetto HttpServletRequest che rappresenta la richiesta HTTP in corso.
     * @return Una risposta conferma che indica che l'utente è stato iscritto con successo alla palestra.
     * Restituisce una risposta con codice HTTP 200 (OK).
     * Nel caso di eccezione sollevate il metodo ritorna una BadRequest 400
     */
    @PostMapping("/iscriviti")
    public ResponseEntity<?> iscriviUtente(@Valid @RequestBody RequestIscrivitiPalestra requestIscrivitiPalestra, HttpServletRequest httpServletRequest){
       try{
           return ResponseEntity.ok(palestraService.iscriviUtentePalestra(httpServletRequest, requestIscrivitiPalestra));
       }catch (EntityNotFoundException ex){
           return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
       }
    }


    /**
     * Elimina una palestra dato l'indirizzo email della palestra.
     *
     * @param email_palestra L'indirizzo email della palestra da eliminare.
     * @return ResponseEntity con un messaggio di successo se l'eliminazione è avvenuta con successo.
     * In caso contrario, restituisce un ResponseEntity con uno stato HTTP BadRequest e il messaggio di errore.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/elimina/{email_palestra}")
    public ResponseEntity<?> elimaPalestra(@PathVariable("email_palestra") String email_palestra){
        try {
            return ResponseEntity.ok(palestraService.eliminaPalestra(email_palestra));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }



}
