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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.DateTimeException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attrezzatura")
public class AttrezzaturaController {

    private final AttrezzaturaService attrezzaturaService;

    /**
     * Gestisce la richiesta GET per ottenere tutte le attrezzature disponibili.
     * @return ResponseEntity contenente la lista di attrezzature disponibili.
     *  nel caso di eccezioni sollevate il metodo ritorna una Internal Server Error 500
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
     *  nel caso di eccezioni sollevate il metodo ritorna una Internal Server Error 500
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
     *  nel caso di eccezioni sollevate il metodo ritorna una bad request 400
     * @param requestAttrezzatura
     * @param request
     * @return
     */
    @PostMapping("/noleggia")
    public ResponseEntity<?> noleggiaAttrazzatura(@Valid @RequestBody RequestNoleggiaAttrezzatura requestAttrezzatura, HttpServletRequest request){
        try{
            return ResponseEntity.ok(DTOManager.toAttrezzaturaResponseByAttrezzatura(attrezzaturaService.noleggiaAttrazzatura(request, requestAttrezzatura)));
        }catch (EntityNotFoundException | IllegalStateException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gestisce la richiesta POST per inserire una nuova attrezzatura.
     * Richiede l'autorizzazione dell'utente con ruolo "ADMIN".
     *
     * @param requestAttrezzatura Oggetto contenente i dettagli della nuova attrezzatura da inserire.
     * @return ResponseEntity che contiene i dettagli dell'attrezzatura appena inserita.
     *  nel caso di eccezioni sollevate il metodo ritorna una bad request 400
     */
    @PostMapping("/inserisci")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> inserisciAttrezzatura(@Valid @RequestBody RequestAttrezzatura requestAttrezzatura){
        try{
            return ResponseEntity.ok(DTOManager.toAttrezzaturaResponseByAttrezzatura(attrezzaturaService.inserisciNuovoAttrezzo(requestAttrezzatura)));
        }catch (EntityNotFoundException | IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

    /**
     * Gestisce la richiesta GET per ottenere tutti i noleggi disponibili.
     * Restituisce una lista di DTO che contengono informazioni sull'attrezzatura, che poi verrà varappata nella classa dto
     * Manager che gi restituisce una lista di tipo response attrezzatura
     * insieme ai dati di noleggio corrispondenti.
     * @return ResponseEntity che contiene la lista di DTO delle attrezzature noleggiate.
     * nel caso di eccezioni sollevate il metodo ritorna una bad request 400
     */
    @GetMapping("getAll/noleggi")
    public ResponseEntity<?> getAllNoleggi(){
        try{
            return  ResponseEntity.ok(DTOManager.listAttrezzatureWithNoleggio(attrezzaturaService.getAllNoleggi()));
        }catch(EntityNotFoundException | IllegalStateException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gestisce la richiesta GET per ottenere tutti i noleggi non ancora finiti.
     * Restituisce una lista di DTO contenenti informazioni sull'attrezzatura
     * insieme ai dati di noleggio non ancora conclusi.
     *
     * @return ResponseEntity che contiene la lista di DTO delle attrezzature con noleggi non finiti.
     */
    @GetMapping("getAll/noleggi_non_finiti")
    public ResponseEntity<?> getAllNoleggiNonFiniti(){
        try{
            return ResponseEntity.ok(DTOManager.listAttrezzatureWithNoleggio(attrezzaturaService.getAllNoleggiNonFiniti()));
        }catch(EntityNotFoundException | IllegalStateException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
