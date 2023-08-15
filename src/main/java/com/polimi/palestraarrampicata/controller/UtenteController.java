package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.service.LezioneService;
import com.polimi.palestraarrampicata.service.UtenteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UtenteController {

    private final UtenteService utenteService;

    @GetMapping("/getAll/Inviti/accettati")
    public ResponseEntity<?> getLessons(HttpServletRequest request){
        try{
            return ResponseEntity.ok(utenteService.getListInvitiLezioneAccettate(request));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getAllInviti")
    public ResponseEntity<?> getAllLessons(HttpServletRequest request){
        try{
            return ResponseEntity.ok(utenteService.getListInvitiLezione(request));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_'+T(com.polimi.palestraarrampicata.model.Ruolo).ADMIN)")
    @DeleteMapping("/delete/user/{email}")
    public ResponseEntity<String> deleteUtente(@PathVariable("email") String email){
        try{
            return ResponseEntity.ok(utenteService.deleteUserByEmail(email));
        }catch (IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("commenta/istruttore")
    public ResponseEntity<?> creaCommento(@Valid @RequestBody RequestCommento requestCommento, BindingResult result, HttpServletRequest request){
        try{
            return ResponseEntity.ok(utenteService.creaCommento(request, requestCommento));
        }catch (IllegalStateException | EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
