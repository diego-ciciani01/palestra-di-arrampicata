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

    @PostMapping(value = "/crea", consumes = "application/json")
    public ResponseEntity<?> createLesson(@Valid @RequestBody RequestLezione requestLezione, HttpServletRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toLessonResponseByLesson(lezioneService.createLesson(requestLezione, request)));
        }catch (IllegalStateException | EntityNotFoundException | DateTimeException  ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getLessons(){
        try{
            return ResponseEntity.ok(lezioneService.getListLession());
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping(value = "/invito/accetta", consumes = "application/json")
    @PreAuthorize("hasAuthority('ISTRUTTORE')")
    public ResponseEntity<?> accettaInvito(@Valid @RequestBody RequestAccettaRiffiuta requestAccettaRifiuta,  HttpServletRequest request){
        try{
            return ResponseEntity.ok(DTOManager.ToResposeIstruttoreByLesson(lezioneService.accettaRifiutaLezione(requestAccettaRifiuta, request)));
        }catch (EntityNotFoundException | IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping("/getAll/byInstructor/{id_instructor}")
    public ResponseEntity<?> getLessonsByInstructor(@PathVariable("id_instructor") Integer idInstructor){
        try{
            return ResponseEntity.ok(lezioneService.getLessionByInstructor(idInstructor));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
