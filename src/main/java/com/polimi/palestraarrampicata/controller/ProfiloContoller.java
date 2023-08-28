package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestLogin;
import com.polimi.palestraarrampicata.dto.request.RequestModificaUtente;
import com.polimi.palestraarrampicata.dto.request.RequestRegistrazione;
import com.polimi.palestraarrampicata.exception.LoginFallito;
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
@RequestMapping("/profilo")
public class ProfiloContoller {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private HttpSession httpSession;

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<String> login(@Valid @RequestBody RequestLogin requestLogin, BindingResult result) {
        try {

            if (result.hasErrors()) {
                return new ResponseEntity<>(Utils.getErrori(result), HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(profileService.login(requestLogin));
        } catch (LoginFallito e) {
            return new ResponseEntity<>(e.getMessage() , HttpStatus.UNAUTHORIZED);
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
            return ResponseEntity.ok(DTOManager.ResponseUtenteFromUtente(profileService.registrazione(requestRegistrazione)));

        }
        catch (RegistrazioneFallita e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/modifica", consumes = "application/json")
    public ResponseEntity<?> modifica(@Valid @RequestBody RequestModificaUtente requestModifica, BindingResult result, HttpServletRequest request) {
        try {

            if (result.hasErrors()) {
                return new ResponseEntity<>(Utils.getErrori(result), HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(profileService.modificaUtente(requestModifica, request));
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*
    @PostMapping(value = "/logout", consumes = "application/json")
    public void logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
*/

}
