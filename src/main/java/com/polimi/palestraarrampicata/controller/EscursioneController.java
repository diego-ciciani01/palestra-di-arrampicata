package com.polimi.palestraarrampicata.controller;

import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestAttrezzatura;
import com.polimi.palestraarrampicata.dto.request.RequestEscursione;
import com.polimi.palestraarrampicata.dto.request.RequestModificaEscursione;
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
     * @param requestEscursione Oggetto contenente i dettagli dell'escursione da creare.
     * @return ResponseEntity contenente l'oggetto ResponseEscursione dell'escursione creata.
     * @throws IllegalStateException Se si verifica uno stato illegale durante la creazione dell'escursione.
     * @throws EntityNotFoundException Se l'entità necessaria per la creazione dell'escursione non viene trovata.
     * @throws DateTimeException Se si verifica un problema con le date nell'escursione.
     * Restituisce una risposta con codice HTTP 200 (OK) e i dettagli dell'escursione appena creata.
     * nel caso di eccezioni sollevate il metodo ritorna una BadRequest 400
     */
    @PostMapping("/crea")
    @PreAuthorize("hasAuthority('ISTRUTTORE')")
    public ResponseEntity<?> creaEscursione(@Valid @RequestBody RequestEscursione requestEscursione){
        try{
            return ResponseEntity.ok(DTOManager.toEscursioneResponseByEscursione(escursioneService.createEscursione(requestEscursione)));
        }catch (IllegalStateException | EntityNotFoundException | DateTimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * questo EndPoint è accessibile a tutti e ritorna tutte l'escursioni disponibili, con lo status
     * Disponibile = True
     * @return ResponseEntity contenente la lista di oggetti Escursione disponibili.
     * @throws EntityNotFoundException Se non vengono trovate escursioni disponibili.
     * Restituisce una risposta con codice HTTP 200 (OK) e i dettagli di tutte l'escursioni dell'escursione.
     * nel caso di eccezioni sollevate il metodo ritorna una Internal Server Errro 500
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
     * @param idEscursione ID dell'escursione a cui l'utente vuole iscriversi.
     * @param httpServletRequest Oggetto HttpServletRequest per ottenere l'utente dalla richiesta.
     * @return ResponseEntity contenente l'oggetto Escursione dell'escursione a cui l'utente si è iscritto.
     * @throws EntityNotFoundException Se l'escursione specificata non viene trovata.
     * Restituisce una risposta con codice HTTP 200 (OK) e l'oggetto dell'escursione a cui ci si è iscritti.
     * nel caso di eccezioni sollevate il metodo ritorna una Internal Server Error 500
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
     * @param idEscursione ID dell'escursione da eliminare.
     * @return ResponseEntity contenente l'oggetto ResponseEscursione dell'escursione eliminata.
     * @throws EntityNotFoundException Se l'escursione specificata non viene trovata.
     * Restituisce una risposta con codice HTTP 200 (OK) per confermare la corretta eliminazione dell'escursione.
     * nel caso di eccezioni sollevate il metodo ritorna una Internal Server Error 500
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
     * @param idIstruttore ID dell'istruttore a cui appartengono le escursioni cercate.
     * @return ResponseEntity contenente la lista di oggetti Escursione associate all'istruttore.
     * @throws EntityNotFoundException Se le escursioni associate all'istruttore non vengono trovate.
     * Restituisce una risposta con codice HTTP 200 (OK) e restituisce la liste di escursioni organizzate
     * dall'istruttore passato nella chiamata.
     * nel caso di eccezioni sollevate il metodo ritorna una Internal Server Error 500
     */
    @GetMapping("/getAll/byIstruttore/{id_istruttore}")
    public ResponseEntity<?> getEscursioneByIstruttore(@PathVariable("id_istruttore") Integer idIstruttore){
        try{
            return ResponseEntity.ok(escursioneService.getListEscursioniByIstruttore(idIstruttore));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/modifica")
    public ResponseEntity<?> modificaEscursione(@Valid @RequestBody RequestModificaEscursione requestModificaEscursione, HttpServletRequest httpServletRequest){
        try{
            return ResponseEntity.ok(escursioneService.modificaEscursione(requestModificaEscursione, httpServletRequest));
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
