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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.DateTimeException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/escursione")
public class EscursioneController {

    private final EscursioneService escursioneService;

    @PostMapping("/crea")
    @PreAuthorize("hasAuthority('ISTRUTTORE')")
    public ResponseEntity<?> creaEscursione(@Valid @RequestBody RequestEscursione RequestEscursione, BindingResult result, HttpServletRequest httpServletRequest){
        try{
            return ResponseEntity.ok(DTOManager.toEscursioneResponseByEscursione(escursioneService.createEscursione(RequestEscursione,httpServletRequest)));
        }catch (IllegalStateException | EntityNotFoundException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
