package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestLogin;
import com.polimi.palestraarrampicata.dto.request.RequestModificaUtente;
import com.polimi.palestraarrampicata.dto.request.RequestRegistrazione;
import com.polimi.palestraarrampicata.exception.LoginFallito;
import com.polimi.palestraarrampicata.exception.LogoutFallito;
import com.polimi.palestraarrampicata.exception.RegistrazioneFallita;
import com.polimi.palestraarrampicata.service.ProfileService;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/profilo")
public class ProfiloContoller {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private HttpSession httpSession;

    /**
     * Gestisce la richiesta di accesso o login di un utente.
     *
     * @param requestLogin Le informazioni di accesso fornite dall'utente.
     * @param result Risultato della validazione dei dati di accesso.
     * @return Una ResponseEntity con il messaggio di successo se l'accesso è avvenuto con successo.
     * @throws LoginFallito Se l'accesso non è riuscito a causa di credenziali errate.
     * @throws EntityNotFoundException Se si è verificato un errore interno nel server durante il login.
     * Restituisce una risposta con codice HTTP 200 (OK) e il jwt token usato per gestire la sicurezza della sessione.
     * Nel caso di eccezioni sollevate il metodo ritorna InternalServerError (500), BadRequest (400) e una Unauthorized (403)
     */
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<String> login(@Valid @RequestBody RequestLogin requestLogin, BindingResult result) {
        try {
            //controllo se ci sono errori nella richiesta
            if (result.hasErrors()) {
                return new ResponseEntity<>(Utils.getErrori(result), HttpStatus.BAD_REQUEST);
            }
            // Esegue il tentativo di access
            return ResponseEntity.ok(profileService.login(requestLogin));
        } catch (LoginFallito e) {
            return new ResponseEntity<>(e.getMessage() , HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gestisce la richiesta di registrazione di un nuovo utente.
     *
     * @param requestRegistrazione Le informazioni di registrazione fornite dall'utente.
     * @param result Risultato della validazione dei dati di registrazione.
     * @return Una ResponseEntity con il messaggio di successo se la registrazione è avvenuta con successo.
     * @throws RegistrazioneFallita Se la registrazione non è riuscita a causa di dati errati o duplicati.
     * @throws EntityNotFoundException Se si è verificato un errore interno nel server durante la registrazione.
     * Restituisce una risposta con codice HTTP 200 (OK) l'oggetto della registrazione.
     * Nel caso di eccezioni sollevate il metodo ritorna InternalServerError (500), BadRequest (400) e una Unauthorized (403)
     */
    @PostMapping(value = "/registrazione", consumes = "application/json")
    public ResponseEntity<?> registrazione(@Valid @RequestBody RequestRegistrazione requestRegistrazione, BindingResult result){
        try{
            // Controlla se ci sono parametri mancanti
            if(!requestRegistrazione.isParametriPresenti()){
                return new ResponseEntity<>("parametri mancanti", HttpStatus.BAD_REQUEST);
            }
            // Controlla se ci sono errori di validazione nei dati di registrazione
            if(result.hasErrors()){
                return  new ResponseEntity<>(Utils.getErrori(result), HttpStatus.BAD_REQUEST);
            }
            // Esegue la registrazione e restituisce la risposta con i dettagli dell'utente registrato
            return ResponseEntity.ok(DTOManager.ResponseUtenteFromUtente(profileService.registrazione(requestRegistrazione)));

        }
        catch (RegistrazioneFallita e){
            // Se la registrazione fallisce a causa di dati errati o duplicati
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (EntityNotFoundException e){
            // Se si verifica un errore interno durante la registrazione
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gestisce la richiesta di modifica dei dettagli di un utente.
     *
     * @param requestModifica Le informazioni di modifica fornite dall'utente.
     * @param result Risultato della validazione dei dati di modifica.
     * @param request HttpServletRequest che rappresenta la richiesta HTTP.
     * @return Una ResponseEntity con il messaggio di successo se la modifica è avvenuta con successo.
     * @throws EntityNotFoundException Se si è verificato un errore interno nel server durante la modifica.
     * Restituisce una risposta con codice HTTP 200 (OK) una stringa per confermare l'avvenuta modifica.
     * Nel caso di eccezioni sollevate il metodo ritorna InternalServerError (500) e BadRequest (400)
     */
    @PostMapping(value = "/modifica", consumes = "application/json")
    public ResponseEntity<?> modifica(@Valid @RequestBody RequestModificaUtente requestModifica, BindingResult result, HttpServletRequest request) {
        try {
            // Controlla se ci sono errori di validazione nei dati di modifica
            if (result.hasErrors()) {
                return new ResponseEntity<>(Utils.getErrori(result), HttpStatus.BAD_REQUEST);
            }
            // Esegue la modifica e restituisce la risposta con il messaggio di successo
            return ResponseEntity.ok(profileService.modificaUtente(requestModifica, request));
        }catch (EntityNotFoundException e){
            // Se si verifica un errore interno durante la modifica
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gestisce la richiesta di logout di un utente.
     *
     * @param request HttpServletRequest che rappresenta la richiesta HTTP.
     * @return Una ResponseEntity con lo stato OK se il logout è avvenuto con successo.
     * @throws LogoutFallito Se si è verificato un errore durante il processo di logout.
     * @throws Exception Se si è verificato un errore interno nel server durante il logout.
     * Restituisce una risposta con codice HTTP 200 (OK).
     * Nel caso di eccezioni sollevate il metodo ritorna InternalServerError (500) e BadRequest (400)
     */
    @GetMapping(value = "logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            // Esegue il logout dell'utente
            profileService.logout(request);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (LogoutFallito e){
            // Se si verifica un errore durante il logout
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            // Se si verifica un errore interno durante il logout
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
