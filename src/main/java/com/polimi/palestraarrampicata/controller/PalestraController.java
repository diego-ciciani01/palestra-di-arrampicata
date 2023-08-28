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
     * in quest EndPoint L'admin è in grado di creare una palestra inserendo tutte le specifiche come:
     * Nome, via, Cap, Citta e telefono della palestra
     * @param requestPalestra
     * @return
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
     * getAll permette di prendere la lista di tutte le palestre gestite dalla piattaforma
     * @return
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPalestre(){
        try{
            return ResponseEntity.ok(palestraService.getAllPalestre());
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getAll/iscrittiByPalestra/{palestra_email}")
    public ResponseEntity<?> getAllUtentiByPalestra(@PathVariable("palestra_email") String palestaEmail){
        try{
              List<ResponseUtente> responseUtenteList = DTOManager.toUserResponseByUsers(palestraService.getAllIscrittiBYEmailPalestra(palestaEmail));
                return ResponseEntity.ok(responseUtenteList);
        }catch (IllegalStateException enf){
            return new ResponseEntity<>(ResponsePalestra.builder().message(enf.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/disiscrivi/{email_utente}")
    public ResponseEntity<?> disiscriviUtente(@PathVariable("email_utente") String email_utente){
        try{
            return ResponseEntity.ok(palestraService.disiscriviUtente(email_utente));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/iscriviti")
    public ResponseEntity<?> iscriviUtente(@Valid @RequestBody RequestIscrivitiPalestra requestIscrivitiPalestra, HttpServletRequest httpServletRequest){
       try{
           return ResponseEntity.ok(palestraService.iscriviUtentePalestra(httpServletRequest, requestIscrivitiPalestra));
       }catch (EntityNotFoundException ex){
           return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
       }
    }


}
