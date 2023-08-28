package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestAttrezzatura;
import com.polimi.palestraarrampicata.dto.request.RequestNoleggiaAttrezzatura;
import com.polimi.palestraarrampicata.dto.response.ResponseAttrezzatura;
import com.polimi.palestraarrampicata.dto.response.ResponseNoleggio;
import com.polimi.palestraarrampicata.exception.NoleggioFallito;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.*;
import com.polimi.palestraarrampicata.repository.*;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class AttrezzaturaService {
    private final AttrezzaturaRepo attrezzaturaRepo;
    private final UtenteRepo utenteRepo;
    private final TagliaRepo tagliaRepo;
    private final PalestraRepo palestraRepo;
    private final NoleggioRepo noleggioRepo;
    private  final JwtUtils jwtUtils;

    /**
     * questo metodo prende una lista di tutte le attrezzature disponibili, prende anche la quantità di
     * prodotto disponibile per ogni sigolo prodotto senza fare distinzioni di tipoologia di taglie
     * @return
     * @throws EntityNotFoundException
     */
    public List<ResponseAttrezzatura> getListAttrezzaturaDisponibile() throws EntityNotFoundException{
        List<Attrezzatura> attrezzature = attrezzaturaRepo.findAllByDisponibilita(true);
        List<ResponseAttrezzatura> attrezzaturaResponse = new ArrayList<>();
        Integer quantita=0;
        for(Attrezzatura attrezzatura: attrezzature){
            for(Taglia t : attrezzatura.getNomeTaglia()){
                quantita += t.getQuantita();
            }
            attrezzaturaResponse.add(ResponseAttrezzatura.builder()
                    .id(attrezzatura.getId().toString())
                    .nomePalestraAppartenente(attrezzatura.getAttrezziPalestra().getNome())
                    .nomeAttrezzo(attrezzatura.getNomeAttrezzatura())
                    .taglia(attrezzatura.getNomeTaglia())
                    .quantitaDisponibile(quantita)
                    .build());
            quantita = 0;
        }
        return attrezzaturaResponse;
    }

    /**
     * In questo metodo andiamo a prendere una lista di taglie disponibili per un attrezzo passato nella
     * request
     * @param requestAttrezzatura
     * @return
     */
    public List<ResponseAttrezzatura> getListAttrezzaturaPerTipo(RequestAttrezzatura requestAttrezzatura){
        Attrezzatura attrezzature = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
        List<ResponseAttrezzatura> attrezzaturaResponse = new ArrayList<>();
        //Integer quantita = tagliaRepo.calcolaSommaQuantita(attrezzature.getId());
        for(Taglia taglia: attrezzature.getNomeTaglia() ){
            attrezzaturaResponse.add(ResponseAttrezzatura.builder()
                    .id(taglia.getId().toString())
                    .nomePalestraAppartenente(attrezzature.getAttrezziPalestra().getNome())
                    .nomeAttrezzo(attrezzature.getNomeAttrezzatura())
                    .taglia(attrezzature.getNomeTaglia())
           //         .quantitaDisponibile(quantita)
                    .build());
        }
        return attrezzaturaResponse;
    }

    /**
     * il noleggio attrezzatura permette di noleggiare un attrezzo da palestra: all'interno del metodo vengono
     * passati: l'utente loggato e la riquest per il noleggio, all'interno del metodo vegono fatti controlli su
     * data di inizio e fine noleggio, presenza o meno dell'attrezzo in magazino, presenza o meno della taglia
     * scelta, nel caso tutti i contrilli risulano superati, il noleggio risulta possibile quindi si passa alla
     * modifica sulla  classe repository, che va ad aggiornare i dati all'interno del db
     * @param httpServletRequest
     * @param requestAttrezzatura
     * @return
     * @throws EntityNotFoundException
     */
    public Attrezzatura noleggiaAttrazzatura(HttpServletRequest httpServletRequest, RequestNoleggiaAttrezzatura requestAttrezzatura) throws EntityNotFoundException{

        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        List<Attrezzatura> attrezzature = attrezzaturaRepo.findAllByDisponibilita(true);
        Attrezzatura attrezzoCercato = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
        Taglia tagliaCercata = tagliaRepo.findByAttrezzoAndTagliaAttrezzo(attrezzoCercato, requestAttrezzatura.getTaglia());

        LocalDateTime inizioNoleggio = Utils.formatterDataTime(requestAttrezzatura.getDataInizioNoleggio());
        LocalDateTime fineNoleggio = Utils.formatterDataTime(requestAttrezzatura.getDataFineNoleggio());
        //controllo sui datti presi
        if(tagliaCercata == null)
            throw new RicercaFallita("la taglia " + requestAttrezzatura.getTaglia() + " non è al momento presente in magazzino");

        if((inizioNoleggio.isBefore(LocalDateTime.now()) || fineNoleggio.isBefore(inizioNoleggio)))
            throw new DateTimeException("le date di inizio e di fine devono essere inserite correttamente, non devono entrare in conflitto tra di loro");

        if(attrezzoCercato == null)
            throw new RicercaFallita("l'attrezzo cercato non è presente in magazzino");

        if(!inizioNoleggio.isAfter(LocalDateTime.now()) || fineNoleggio.isBefore(inizioNoleggio))
            throw new IllegalStateException("Date inserite non valide");

        if(tagliaCercata.getQuantita() <= 0){
            throw new IllegalStateException("non ci sono rifornimenti in magazzino da poter noleggiare per questo attrezzo");
        }

        if(attrezzoCercato.getDisponibilita() && tagliaCercata.getQuantita() >= Integer.parseInt(requestAttrezzatura.getQuantita())){
           // in questo caso il noleggio è possibile
            Noleggio nuovoNoleggio = new Noleggio(inizioNoleggio, fineNoleggio,utenteLoggato);
            nuovoNoleggio.setAttrezzoNoleggiato(attrezzoCercato);
            if(tagliaCercata.getQuantita() - Integer.parseInt(requestAttrezzatura.getQuantita()) == 0){
                tagliaCercata.setQuantita(0);
                attrezzoCercato.setDisponibilita(false);
            }else {
                tagliaCercata.setQuantita(tagliaCercata.getQuantita() - Integer.parseInt(requestAttrezzatura.getQuantita()));
            }

            if(attrezzoCercato.getNoleggi().isEmpty()){
                 List<Noleggio>  noleggioList = new ArrayList<>();
                 noleggioList.add(nuovoNoleggio);
                 attrezzoCercato.setNoleggi(noleggioList);

            }else {
                utenteLoggato.getAttrezzatureNoleggiate().add(nuovoNoleggio);
            }
            tagliaRepo.save(tagliaCercata);
            noleggioRepo.save(nuovoNoleggio);
            attrezzaturaRepo.save(attrezzoCercato);

        }else {
            throw  new NoleggioFallito("non ci sono abbastanza scorte di prodotto per la quantità richiesta");
        }
    return attrezzoCercato;

    }


    /**
     * in questo metodo andiamo ad inserire un nuovo attrezzo a partire da una request(DTO), le date da stringe
     * vengono convertire, si vede: se l'attrezzo inserito è nuovo, se l'attrezzo non è nuovo ma la taglia inserita riferita
     * all'attrezzo si, e se sia l'attrezzo che la taglia esistono già viene aggirnata solamente la quantità disponibile per l'attrezzo
     * nel primo caso è per quel particolare attrezzo andiamo a creare anche una nuova taglia
     * per quel particolare attrezzo
     * @param requestAttrezzatura
     * @return
     * @throws EntityNotFoundException
     */
    public Attrezzatura inserisciNuovoAttrezzo(RequestAttrezzatura requestAttrezzatura) throws EntityNotFoundException{
        Attrezzatura attrezzo = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
        Palestra palestra = palestraRepo.findById(Integer.parseInt(requestAttrezzatura.getIdPalestraPossessore())).get();
        Taglia tagliaCercata = tagliaRepo.findByAttrezzoAndTagliaAttrezzo(attrezzo, requestAttrezzatura.getTaglia());
        if(palestra == null)throw new EntityNotFoundException("la palestra inserita non esiste");

        List<Taglia> listaTaglie;
        Taglia taglia;

        // caso in cui sia l'attrezzo inserito che la taglia sono nuove
        if(attrezzo ==null && tagliaCercata== null) {
            attrezzo = new Attrezzatura();
            attrezzo.setNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
            attrezzo.setDisponibilita(true);
            attrezzo.setAttrezziPalestra(palestra);

            taglia = new Taglia(requestAttrezzatura.getTaglia().toString(), attrezzo, Integer.parseInt(requestAttrezzatura.getQuantita()));
            listaTaglie = new ArrayList<>();
            listaTaglie.add(taglia);
            attrezzo.setNomeTaglia(listaTaglie);
            taglia.setAttrezzo(attrezzo);

            attrezzaturaRepo.save(attrezzo);
            tagliaRepo.save(taglia);

        }else if(attrezzo !=null && tagliaCercata==null){
            //situazione in cui l'attrezzo esiste già, ma la taglia è nuova
            taglia = new Taglia(requestAttrezzatura.getTaglia().toString(), attrezzo, Integer.parseInt(requestAttrezzatura.getQuantita()));
            taglia.setAttrezzo(attrezzo);
            attrezzo.getNomeTaglia().add(taglia);

            attrezzaturaRepo.save(attrezzo);
            tagliaRepo.save(taglia);

        }else {
            // situazione in cui sia l'attrezzo che la taglia inserita esistono già, quello che andiamo a fare
            // è l'aggiornamento della quantità di attrezzi disponobili per quella taglia
            tagliaCercata.setQuantita(tagliaCercata.getQuantita() + Integer.parseInt(requestAttrezzatura.getQuantita()));
            tagliaRepo.save(tagliaCercata);
        }
        return attrezzo;
    }

    /**
     * con questo metodo permette di ottenere tutti i noleggi presenti in db
     * @return
     * @throws EntityNotFoundException
     */
    public List <Attrezzatura>  getAllNoleggi() throws EntityNotFoundException{
        Iterable<Attrezzatura> attrezzi = attrezzaturaRepo.findAll();
        List<Attrezzatura> attrezziCercati= new ArrayList<>();
        attrezzi.forEach(item -> attrezziCercati.add(item));

        return attrezziCercati;
    }

    public List<Attrezzatura> getAllNoleggiNonFiniti() throws EntityNotFoundException{
        Iterable<Attrezzatura> attrezzi = attrezzaturaRepo.findAll();
        ArrayList<Noleggio> noleggi = new ArrayList<>();
        List<Attrezzatura> attrezziCercati= new ArrayList<>();

        for(Attrezzatura attrezzatura:attrezzi) {
            for (Noleggio noleggio : attrezzatura.getNoleggi()) {
                if (noleggio.getDataNoleggio().isAfter(LocalDateTime.now()))
                    noleggi.add(noleggio);
            }
            attrezzatura.setNoleggi(noleggi);
            attrezziCercati.add(attrezzatura);
        }

        return attrezziCercati;
    }


}
