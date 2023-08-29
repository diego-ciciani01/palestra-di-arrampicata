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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.DateTimeException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attrezzatura")
public class AttrezzaturaController {

    private final AttrezzaturaService attrezzaturaService;

    /**
     * getAll ci restituisce tutta l'attrezzatura ch'è disponibile al noleggio
     * @return
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllEquipment(){
        try{
            return ResponseEntity.ok(attrezzaturaService.getListAttrezzaturaDisponibile());
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * questo EndPoint serve per poter ottenere la lista delle attrezzature divise per tipo
     * e con le relative taglie disponibili per ogni attrezzo, con la quantità noleggiabile
     * @param requestAttrezzatura
     * @return
     */

    @GetMapping("/getAll/type/")
    public ResponseEntity<?> getAllEquipmentType(@Valid @RequestBody RequestAttrezzatura requestAttrezzatura){
        try {
            return  ResponseEntity.ok(attrezzaturaService.getListAttrezzaturaPerTipo(requestAttrezzatura));

        }catch (EntityNotFoundException ex){
            return  new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * questo EndPoint è accessibile sia agli utenti che agli istruttori, da la possibilita di noleggiare dell'attrezzatura
     * facendo una richiesta di tipo noleggiaAttrezzatura
     * @param requestAttrezzatura
     * @param result
     * @param request
     * @return
     */
    @PostMapping("/noleggia")
    public ResponseEntity<?> noleggiaAttrazzatura(@Valid @RequestBody RequestNoleggiaAttrezzatura requestAttrezzatura, BindingResult result, HttpServletRequest request){
        try{
            return ResponseEntity.ok(DTOManager.toAttrezzaturaResponseByAttrezzatura(attrezzaturaService.noleggiaAttrazzatura(request, requestAttrezzatura)));
        }catch (EntityNotFoundException | IllegalStateException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * l'inserimento degli attrezzi nel magazzino è un'operazione destinata solamente all'amministretore di sistema
     * @param requestAttrezzatura
     * @param result
     * @return
     */
    @PostMapping("/inserisci")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> inserisciAttrezzatura(@Valid @RequestBody RequestAttrezzatura requestAttrezzatura, BindingResult result){
        try{
            return ResponseEntity.ok(DTOManager.toAttrezzaturaResponseByAttrezzatura(attrezzaturaService.inserisciNuovoAttrezzo(requestAttrezzatura)));
        }catch (EntityNotFoundException | IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

    /**
     * con questo EndPoint è possibbile ottenere la lista dei noleggi effattuati
     * @return
     */
    @GetMapping("getAll/noleggi")
    public ResponseEntity<?> getAllNoleggi(){
        try{
            return  ResponseEntity.ok(DTOManager.listAttrezzatureWithNoleggio(attrezzaturaService.getAllNoleggi()));
        }catch(EntityNotFoundException | IllegalStateException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("getAll/noleggi_non_finiti")
    public ResponseEntity<?> getAllNoleggiNonFiniti(){
        try{
            return ResponseEntity.ok(DTOManager.listAttrezzatureWithNoleggio(attrezzaturaService.getAllNoleggiNonFiniti()));
        }catch(EntityNotFoundException | IllegalStateException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
