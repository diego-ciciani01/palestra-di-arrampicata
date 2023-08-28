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
     * @param request
     * @return
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
     * @param request
     * @return
     */
    @GetMapping("/getAllInviti")
    public ResponseEntity<?> getAllLessons(HttpServletRequest request){
        try{
            return ResponseEntity.ok(utenteService.getListInvitiLezione(request));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/email/{email}")
    public ResponseEntity<String> deleteUtente(@PathVariable("email") String email){
        try{
            return ResponseEntity.ok(utenteService.deleteUserByEmail(email));
        }catch (IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("commenta/istruttore")
    public ResponseEntity<?> creaCommento(@Valid @RequestBody RequestCommento requestCommento, HttpServletRequest request){
        try{
             ResponseCommento responseCommento =  DTOManager.ToResponseCommentoBYCommento(utenteService.creaCommento(request, requestCommento));
            return ResponseEntity.ok(responseCommento);
        }catch (IllegalStateException | EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PreAuthorize("hasAuthority('UTENTE')")
    @PostMapping("valuta/istruttore")
    public ResponseEntity<?> creaValutazione(@Valid @RequestBody RequestValutazione requestValutazione, BindingResult result, HttpServletRequest request){
        try{
            return ResponseEntity.ok(utenteService.creaValutazione(request, requestValutazione));
        }catch (IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("commenti/getAllByIstruttore/{id_istruttore}")
    public ResponseEntity<?> getAllCommenti(@PathVariable("id_istruttore") Integer idIstruttore, HttpServletRequest httpServletRequest){
        try {
            return ResponseEntity.ok(utenteService.getListCommentifromUtenteToIstruttore(httpServletRequest, idIstruttore));

        }catch (IllegalStateException | EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /*
    @PostMapping("valuta/getAvg/{email_istruttore}")
    public ResponseEntity<?> getAvgValutazioniByIstruttore(@PathVariable("email_istruttore") String email) {
        try {
            return ResponseEntity.ok(utenteService.getAvgValutazione(email));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

     */

}
