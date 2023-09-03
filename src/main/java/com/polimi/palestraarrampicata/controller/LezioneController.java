package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestLezione;
import com.polimi.palestraarrampicata.service.LezioneService;
import com.polimi.palestraarrampicata.dto.request.RequestAccettaRiffiuta;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import java.time.DateTimeException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lezione")
public class LezioneController {

    @Autowired
    private HttpSession httpSession;
    @Autowired
    private LezioneService lezioneService;

    /**
     * Crea una nuova lezione.
     *
     * @param requestLezione Oggetto RequestLezione contenente i dettagli della lezione da creare.
     * @param request Oggetto HttpServletRequest per ottenere informazioni sulla richiesta.
     * @return ResponseEntity contenente l'oggetto ResponseLezione rappresentante la lezione creata.
     * Restituisce una risposta con codice HTTP 200 (OK) e i dettagli della lezione appena creata.
     * nel caso di eccezioni sollevate il metodo ritorna una BadRequest 400
     */
    @PostMapping(value = "/crea", consumes = "application/json")
    public ResponseEntity<?> createLesson(@Valid @RequestBody RequestLezione requestLezione, HttpServletRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toLessonResponseByLesson(lezioneService.createLesson(requestLezione, request)));
        }catch (IllegalStateException | EntityNotFoundException | DateTimeException  ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Ottiene la lista di tutte le lezioni disponibili.
     *
     * @return ResponseEntity contenente la lista di oggetti ResponseLezione rappresentanti le lezioni disponibili.
     * Restituisce una risposta con codice HTTP 200 (OK) la lista della lezioni trovate.
     * nel caso di eccezioni sollevate il metodo ritorna una Internal Server Error 500
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getLessons(){
        try{
            return ResponseEntity.ok(lezioneService.getListLession());
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Gestisce l'accettazione o il rifiuto di un invito a una lezione da parte di un istruttore.
     *
     * @param requestAccettaRifiuta Oggetto RequestAccettaRifiuta contenente i dettagli dell'invito.
     * @param request Oggetto HttpServletRequest per accedere alle informazioni sulla richiesta HTTP.
     * @return ResponseEntity contenente l'oggetto ResponseIstruttore rappresentante l'istruttore dopo l'accettazione/rifiuto dell'invito.
     * Restituisce una risposta con codice HTTP 200 (OK) e l'oggetto della lezione accettata.
     * nel caso di eccezioni sollevate il metodo ritorna una Bad Request 400.
     */

    @PostMapping(value = "/invito/accetta", consumes = "application/json")
    @PreAuthorize("hasAuthority('ISTRUTTORE')")
    public ResponseEntity<?> accettaInvito(@Valid @RequestBody RequestAccettaRiffiuta requestAccettaRifiuta,  HttpServletRequest request){
        try{
            return ResponseEntity.ok(DTOManager.ToResposeIstruttoreByLesson(lezioneService.accettaRifiutaLezione(requestAccettaRifiuta, request)));
        }catch (EntityNotFoundException | IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }
    /**
     * Ottiene tutte le lezioni associate a un istruttore specificato.
     *
     * @param idInstructor L'ID dell'istruttore di cui si vogliono ottenere le lezioni.
     * @return ResponseEntity contenente una lista di ResponseLezione rappresentanti le lezioni associate all'istruttore.
     * Restituisce una risposta con codice HTTP 200 (OK) e la lista delle lezioni dell'id dell'istruttore passato nella chiamata.
     * nel caso di eccezioni sollevate il metodo ritorna una Internal Server Error 500.
     */
    @GetMapping("/getAll/byInstructor/{id_instructor}")
    public ResponseEntity<?> getLessonsByInstructor(@PathVariable("id_instructor") Integer idInstructor){
        try{
            return ResponseEntity.ok(lezioneService.getLessionByInstructor(idInstructor));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
