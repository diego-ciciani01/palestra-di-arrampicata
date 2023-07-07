package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.request.RequestLogin;
import com.polimi.palestraarrampicata.dto.request.RequestRegistrazione;
import com.polimi.palestraarrampicata.dto.response.ResponseLogin;
import com.polimi.palestraarrampicata.exception.LoginFallito;
import com.polimi.palestraarrampicata.exception.RegistrazioneFallita;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.service.UtenteService;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.JoinColumn;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/utente")
public class UtenteContoller {
    @Autowired
    private UtenteService utenteService;
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> login(@Valid @RequestBody RequestLogin requestLogin, BindingResult result) {
        try {
            if (!requestLogin.isParametriPresenti()) {
                return new ResponseEntity<>("mancano i parametri", HttpStatus.BAD_REQUEST);
            }
            if (result.hasErrors()) {
                return new ResponseEntity<>(Utils.getErrori(result), HttpStatus.BAD_REQUEST);
            }

            Utente utente = new Utente();
            utente.setUsername(requestLogin.getUsername());
            utente.setPassword(requestLogin.getPassword());
            utente = utenteService.findByUsername(requestLogin.getUsername());
            if(utente == null){
                throw new LoginFallito("utento non trovato");
            }
            ResponseLogin responseUtenteLogin = new ResponseLogin(utente);
            return new ResponseEntity<>(responseUtenteLogin, HttpStatus.OK);
        } catch (LoginFallito e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping(value = "/registrazione", consumes = "application/json")
    public ResponseEntity<?> registrazione(@Valid @RequestBody RequestRegistrazione requestRegistrazione, BindingResult result){
        try{
            if(!requestRegistrazione.isParametriPresenti()){
                return new ResponseEntity<>("parametri mancanti", HttpStatus.BAD_REQUEST);
            }
            if(result.hasErrors()){
                return  new ResponseEntity<>(Utils.getErrori(result), HttpStatus.BAD_REQUEST);
            }
            utenteService.registrazione(requestRegistrazione);
            return new ResponseEntity<String>(HttpStatus.OK);

        }
        catch (RegistrazioneFallita e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
