package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestAttrezzatura;
import com.polimi.palestraarrampicata.dto.request.RequestNoleggiaAttrezzatura;
import com.polimi.palestraarrampicata.service.AttrezzaturaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.DateTimeException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attrezzatura")
public class AttrezzaturaController {

    private final AttrezzaturaService attrezzaturaService;

    /**
     * getAll ci restituisce tutta l'attrezzatura ch'è disponibile al noleggio
     * @param request
     * @return
     */
    @PostMapping("/getAll")
    public ResponseEntity<?> getAllequipment(HttpServletRequest request){
        try{
            return ResponseEntity.ok(attrezzaturaService.getListAttrezzaturaDisponibile(request));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<?> getAllequipmentType(@Valid @RequestBody RequestAttrezzatura requestAttrezzatura){
        try {
            return  ResponseEntity.ok(attrezzaturaService.getListAttrezzaturaPerTipo(requestAttrezzatura));

        }catch (EntityNotFoundException ex){
            return  new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/noleggia")
    public ResponseEntity<?> noleggiaAttrazzatura(@Valid @RequestBody RequestNoleggiaAttrezzatura requestAttrezzatura, BindingResult result, HttpServletRequest request){
        try{
            return ResponseEntity.ok(DTOManager.toAttrezzaturaResponseByAttrezzatura(attrezzaturaService.noleggiaAttrazzatura(request, requestAttrezzatura)));
        }catch (EntityNotFoundException | IllegalStateException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/inserisci")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> inserisciAttrezzatura(@Valid @RequestBody RequestAttrezzatura requestAttrezzatura, BindingResult result){
        try{
            return ResponseEntity.ok(DTOManager.toAttrezzaturaResponseByAttrezzatura(attrezzaturaService.inserisciNuovoAttrezzo(requestAttrezzatura)));
        }catch (EntityNotFoundException | IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

        }

    }

}
