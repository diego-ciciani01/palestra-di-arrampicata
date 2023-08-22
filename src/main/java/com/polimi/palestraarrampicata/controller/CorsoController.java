package com.polimi.palestraarrampicata.controller;


import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestCorso;
import com.polimi.palestraarrampicata.dto.request.RequestIscriviti;
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
     * @param requestCorso
     * @return
     */
    @PostMapping("/crea")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> creaCorso(@Valid @RequestBody RequestCorso requestCorso) {
        try {
            return ResponseEntity.ok(DTOManager.toCorsoResponseByCorso(corsoService.creaCorso(requestCorso)));
        } catch (EntityNotFoundException | IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * In questo EndPoint solo L'admin è in grado di eliminare un corso passando l'id di quest'ultimo
     * @param idCorso
     * @return
     */

    @PostMapping("/elimina/{id_corso}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> eliminaCorso(@PathVariable("id_corso") Integer idCorso){
        try{
            return ResponseEntity.ok(corsoService.eliminaCorso(idCorso));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getLessons(){
        try{
            return ResponseEntity.ok(corsoService.getListCorso());
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>("non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAll/byInstructor/{id_instructor}")
    public ResponseEntity<?> getLessonsByInstructor(@PathVariable("id_instructor") Integer idInstructor){
        try{
            return ResponseEntity.ok(corsoService.getLessionByInstructor(idInstructor));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/iscriviti")
    @PreAuthorize("hasAuthority('UTENTE')")
    public ResponseEntity<?> iscrivitiCorso(@Valid @RequestBody RequestIscriviti requestIscriviti, BindingResult result, HttpServletRequest httpServletRequest){
        try{
            return ResponseEntity.ok(corsoService.iscrivitiCorso(requestIscriviti, httpServletRequest));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
