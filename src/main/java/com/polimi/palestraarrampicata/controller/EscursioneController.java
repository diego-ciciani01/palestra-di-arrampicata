package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestAttrezzatura;
import com.polimi.palestraarrampicata.dto.request.RequestEscursione;
import com.polimi.palestraarrampicata.model.Escursione;
import com.polimi.palestraarrampicata.service.EscursioneService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.DateTimeException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/escursione")
public class EscursioneController {

    private final EscursioneService escursioneService;

    /**
     * In questo EndPoint è possibile creare una escursione
     * solamente l'istruttore può creare un uscita
     * @param RequestEscursione
     * @param result
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/crea")
    @PreAuthorize("hasAuthority('ISTRUTTORE')")
    public ResponseEntity<?> creaEscursione(@Valid @RequestBody RequestEscursione RequestEscursione, BindingResult result, HttpServletRequest httpServletRequest){
        try{
            return ResponseEntity.ok(DTOManager.toEscursioneResponseByEscursione(escursioneService.createEscursione(RequestEscursione,httpServletRequest)));
        }catch (IllegalStateException | EntityNotFoundException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * questo EndPoint è accessibile a tutti e ritorna tutte l'escursioni disponibili, con lo status
     * Disponibile = True
     * @return
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(){
        try {
            return ResponseEntity.ok(escursioneService.getListEscursioniDisponibili());
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * In questo EndPoint L'utente è in grado si iscriversi ad una escursione organizzata da un istruttore
     * passando nell'url Id dell'escursione
     * @param idEscursione
     * @param httpServletRequest
     * @return
     */
    @PreAuthorize("hasAuthority('UTENTE')")
    @PostMapping("/iscriviti/{id_escursione}")
    public ResponseEntity<?> iscriviti(@PathVariable("id_escursione") Integer idEscursione, HttpServletRequest httpServletRequest){
        try{
            return ResponseEntity.ok(escursioneService.partecipaEscursione(idEscursione, httpServletRequest));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * In questo EndPoint L'istruttore può eliminare una escursione
     * @param idEscursione
     * @return
     */
    @DeleteMapping("/elimina/{id_escursione}")
    @PreAuthorize("hasAuthority('ISTRUTTORE')")
    public ResponseEntity<?> eliminaEscursione(@PathVariable("id_escursione") Integer idEscursione){
        try{
            return ResponseEntity.ok(escursioneService.eliminaEscursione(idEscursione));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * In questo EndPoint tutti possono cercare tra tutte le Escursioni disponibili organizzate da
     * un istruttore, di cui l'id deve essere passato nell'indirizzo url
     * @param idIstruttore
     * @return
     */

    @GetMapping("/getAll/byIstruttore/{id_istruttore}")
    public ResponseEntity<?> getEscursioneByIstruttore(@PathVariable("id_istruttore") Integer idIstruttore){
        try{
            return ResponseEntity.ok(escursioneService.getListEscursioniByIstruttore(idIstruttore));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
