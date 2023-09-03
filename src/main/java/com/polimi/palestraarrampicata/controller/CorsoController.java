package com.polimi.palestraarrampicata.controller;


import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestCorso;
import com.polimi.palestraarrampicata.dto.request.RequestIscriviti;
import com.polimi.palestraarrampicata.dto.response.ResponseCorso;
import com.polimi.palestraarrampicata.service.CorsoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/corso")
public class CorsoController {

    private final CorsoService corsoService;

    /**
     * In questo endpoint ADMIN può creare un corso e assegnarlo ad un istruttore
     * @param requestCorso Oggetto contenente i dettagli del corso da creare.
     * @return ResponseEntity contenente la risposta con i dettagli del corso appena creato.
     * Restituisce una risposta con codice HTTP 200 (OK) e i dettagli del corso appena creato.
     * nel caso di eccezioni sollevate il metodo ritorna una BadRequest 400
     */

    @PostMapping("/crea")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> creaCorso(@Valid @RequestBody RequestCorso requestCorso) {
        try {
             ResponseCorso responseCorso = DTOManager.toCorsoResponseByCorso(corsoService.creaCorso(requestCorso));
            return ResponseEntity.ok(responseCorso);
        } catch (EntityNotFoundException | IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * In questo EndPoint solo L'admin è in grado di eliminare un corso passando l'id di quest'ultimo
     * @param idCorso ID del corso da eliminare.
     * @return ResponseEntity contenente la risposta con conferma dell'eliminazione.
     * Restituisce una risposta con codice HTTP 200 (OK) per confermare l'eliminazione del corso.
     * nel caso di eccezioni sollevate il metodo ritorna una Internal Server Error 500
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/elimina/{id_corso}")
    public ResponseEntity<?> eliminaCorso(@PathVariable("id_corso") Integer idCorso){
        try{
            return ResponseEntity.ok(corsoService.eliminaCorso(idCorso));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Ottiene una lista di tutti i corsi disponibili nel sistema.
     * @return ResponseEntity contenente la risposta con la lista dei corsi.
     * Restituisce una risposta con codice HTTP 200 (OK) e la lista dei corsi.
     * Creare una risposta con un codice HTTP 500 (Internal Server Error) e un messaggio di errore.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllCorsi(){
        try{
            return ResponseEntity.ok(corsoService.getListCorso());
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Restituisce una risposta contenente la lista di corsi visibili per l'utente autenticato.
     *
     * @param httpServletRequest La richiesta HTTP effettuata.
     * @return Una ResponseEntity contenente la lista di corsi visibili per l'utente.
     */
    @GetMapping("getAll/corsiVisibili")
    public  ResponseEntity<?> getAllCorsiVisibili(HttpServletRequest httpServletRequest){
        try{
            return ResponseEntity.ok(corsoService.getAllCorsiVisibiliUtente(httpServletRequest));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Ottiene una lista di tutti i corsi tenuti da un istruttore specifico.
     * @param idInstructor ID dell'istruttore di cui ottenere i corsi.
     * @return ResponseEntity contenente la risposta con la lista dei corsi dell'istruttore.
     * Restituisce una risposta con codice HTTP 200 (OK) e la lista dei corsi dell'istruttore.
     * Creare una risposta con un codice HTTP 500 (Internal Server Error) e includere il messaggio dell'eccezione.
     */
    @GetMapping("/getAll/byInstructor/{id_instructor}")
    public ResponseEntity<?> getLessonsByInstructor(@PathVariable("id_instructor") Integer idInstructor){
        try{
            return ResponseEntity.ok(corsoService.getLessionByInstructor(idInstructor));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Restituisce una risposta contenente una lista di corsi in base alla difficoltà specificata.
     *
     * @param difficolta La difficoltà dei corsi da cercare.
     * @return Una ResponseEntity contenente la lista di corsi che corrispondono alla difficoltà specificata.
     * Gestisce l'eccezione EntityNotFoundException se si verifica durante l'operazione
     */
    @GetMapping("/getAll/byDifficolta/{difficolta}")
    public ResponseEntity<?> getLessonsByDifficolta(@PathVariable("difficolta") String difficolta){
        try{
            return ResponseEntity.ok(corsoService.getCorsiByDifficolta(difficolta));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Iscrive un utente a un corso nel sistema.
     * @param requestIscriviti Oggetto contenente i dettagli dell'iscrizione al corso.
     * @param httpServletRequest Oggetto HttpServletRequest per ottenere l'utente dalla richiesta.
     * @return ResponseEntity contenente la risposta con i dettagli dell'iscrizione al corso.
     * Restituisce una risposta con codice HTTP 200 (OK) e i dettagli dell'iscrizione al corso.
     * Creare una risposta con un codice HTTP 500 (Internal Server Error) e includere il messaggio dell'eccezione.
     */
    @PostMapping(value = "/iscriviti")
    @PreAuthorize("hasAuthority('UTENTE')")
    public ResponseEntity<?> iscrivitiCorso(@Valid @RequestBody RequestIscriviti requestIscriviti, HttpServletRequest httpServletRequest){
        try{
            return ResponseEntity.ok(corsoService.iscrivitiCorso(requestIscriviti, httpServletRequest));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
