package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestPalestra;
import com.polimi.palestraarrampicata.dto.response.ResponsePalestra;
import com.polimi.palestraarrampicata.service.PalestraService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/palestra")
@RequiredArgsConstructor
public class PalestraController {
    private final PalestraService palestraService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ResponsePalestra> createPalestra(@Valid @RequestBody RequestPalestra requestPalestra){
        try {
            return ResponseEntity.ok(DTOManager.toPalestraResponseByPalestra(palestraService.createPalestra(requestPalestra)));
        }catch (IllegalStateException enf){

            return new ResponseEntity<>(ResponsePalestra.builder().message(enf.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("getAll")
    public ResponseEntity<?> getAllPalestre(){
        try{
            return ResponseEntity.ok(palestraService.getAllPalestre());
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
