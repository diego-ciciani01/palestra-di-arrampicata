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
        // otteniamo la lista di attrezzatura disponibili dalla classe repositoty di attrezzatura
        List<Attrezzatura> attrezzature = attrezzaturaRepo.findAllByDisponibilita(true);
        List<ResponseAttrezzatura> attrezzaturaResponse = new ArrayList<>();

        // Variabile per la quantità totale di taglie disponibili.
        Integer quantita=0;

        // Itera attraverso le attrezzature disponibili per costruire le risposte.
        for(Attrezzatura attrezzatura: attrezzature){
            for(Taglia t : attrezzatura.getNomeTaglia()){
                quantita += t.getQuantita();
            }
            // Costruisce l'oggetto ResponseAttrezzatura e lo aggiunge alla lista.
            attrezzaturaResponse.add(ResponseAttrezzatura.builder()
                    .id(attrezzatura.getId().toString())
                    .nomePalestraAppartenente(attrezzatura.getAttrezziPalestra().getNome())
                    .nomeAttrezzo(attrezzatura.getNomeAttrezzatura())
                    .taglia(attrezzatura.getNomeTaglia())
                    .quantitaDisponibile(quantita)
                    .build());

            quantita = 0;// Reimposta la quantità per la prossima attrezzatura.
        }
        return attrezzaturaResponse;
    }

    /**
     * In questo metodo andiamo a prendere una lista di taglie disponibili per un attrezzo passato nella Requrst
     * @param requestAttrezzatura Oggetto contenente il nome dell'attrezzo per il quale ottenere le informazioni.
     * @return Lista di oggetti ResponseAttrezzatura rappresentanti le attrezzature corrispondenti al tipo specificato.
     */

    public List<ResponseAttrezzatura> getListAttrezzaturaPerTipo(RequestAttrezzatura requestAttrezzatura){
        // Ottiene l'attrezzatura corrispondente al nome specificato dalla repository attrezzaturaRepo.
        Attrezzatura attrezzature = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
        if(attrezzature == null) throw new RicercaFallita("l'attrezzo inserito non è presente in magazzino");
        List<ResponseAttrezzatura> attrezzaturaResponse = new ArrayList<>();
        // Itera attraverso le taglie dell'attrezzatura per costruire le risposte.
        for(Taglia taglia: attrezzature.getNomeTaglia() ){
            attrezzaturaResponse.add(ResponseAttrezzatura.builder()
                    .id(taglia.getId().toString())
                    .nomePalestraAppartenente(attrezzature.getAttrezziPalestra().getNome())
                    .nomeAttrezzo(attrezzature.getNomeAttrezzatura())
                    .taglia(attrezzature.getNomeTaglia())
                    .build());
        }
        return attrezzaturaResponse;
    }

    /**
     * il noleggio attrezzatura permette di noleggiare un attrezzo da palestra: all'interno del metodo vengono
     * passati: l'utente loggato e la riquest per il noleggio, all'interno del metodo vegono fatti controlli su
     * data di inizio e fine noleggio, presenza o meno dell'attrezzo in magazino, presenza o meno della taglia
     * scelta, nel caso tutti i contrilli risulano superati, il noleggio risulta possibile quindi si passa alla
     * modifica sulla  classe repository, che va ad aggiornare i dati all'interno del db * @param httpServletRequest Oggetto HttpServletRequest che rappresenta la richiesta HTTP.
     * @param requestAttrezzatura Oggetto contenente i dettagli dell'attrezzatura da noleggiare.
     * @return L'attrezzatura che è stata noleggiata.
     * @throws EntityNotFoundException Se l'attrezzatura o la taglia specificata non possono essere trovate.
     */
    public Attrezzatura noleggiaAttrazzatura(HttpServletRequest httpServletRequest, RequestNoleggiaAttrezzatura requestAttrezzatura) throws EntityNotFoundException{
        // Ottiene l'utente loggato dalla richiesta HTTP.
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Ottiene la lista di attrezzature disponibili dalla repository attrezzaturaRepo.
        List<Attrezzatura> attrezzature = attrezzaturaRepo.findAllByDisponibilita(true);

        // Ottiene l'attrezzatura corrispondente al nome specificato dalla repository attrezzaturaRepo.
        Attrezzatura attrezzoCercato = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());

        // Ottiene la taglia corrispondente all'attrezzatura e alla taglia specificate dalla repository tagliaRepo.
        Taglia tagliaCercata = tagliaRepo.findByAttrezzoAndTagliaAttrezzo(attrezzoCercato, requestAttrezzatura.getTaglia());

        // Converte le date in formato LocalDateTime, utilizzando la classe di support Utils
        LocalDateTime inizioNoleggio = Utils.formatterDataTime(requestAttrezzatura.getDataInizioNoleggio());
        LocalDateTime fineNoleggio = Utils.formatterDataTime(requestAttrezzatura.getDataFineNoleggio());

        //controllo sui datti presi e validità sui dati
        if(tagliaCercata == null)
            throw new RicercaFallita("la taglia " + requestAttrezzatura.getTaglia() + " non è al momento presente in magazzino");

        if((inizioNoleggio.isBefore(LocalDateTime.now()) || fineNoleggio.isBefore(inizioNoleggio)))
            throw new DateTimeException("le date di inizio e di fine devono essere inserite correttamente, non devono entrare in conflitto tra di loro");

        if(attrezzoCercato == null)
            throw new RicercaFallita("l'attrezzo cercato non è presente in magazzino");

        if(!inizioNoleggio.isAfter(LocalDateTime.now()) || fineNoleggio.isBefore(inizioNoleggio))
            throw new IllegalStateException("Date inserite non valide");

        // controllo sulla quantità di pezzi disponibili in magazzino
        if(tagliaCercata.getQuantita() <= 0){
            throw new IllegalStateException("non ci sono rifornimenti in magazzino da poter noleggiare per questo attrezzo");
        }

        if(attrezzoCercato.getDisponibilita() && tagliaCercata.getQuantita() >= Integer.parseInt(requestAttrezzatura.getQuantita())){
           // in questo caso il noleggio è possibile, esecuzione delle operazioni di repository
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
     * @param requestAttrezzatura Oggetto contenente i dettagli dell'attrezzatura da inserire o aggiornare.
     * @return L'attrezzatura appena inserita o aggiornata.
     * @throws EntityNotFoundException Se la palestra specificata non esiste.
     */
    public Attrezzatura inserisciNuovoAttrezzo(RequestAttrezzatura requestAttrezzatura) throws EntityNotFoundException{
        // Cerca l'attrezzatura corrispondente al nome specificato dalla repository attrezzaturaRepo.
        Attrezzatura attrezzo = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());

        // Ottiene la palestra corrispondente all'ID specificato dalla repository palestraRepo.
        Palestra palestra = palestraRepo.findById(Integer.parseInt(requestAttrezzatura.getIdPalestraPossessore())).get();

        // Cerca la taglia corrispondente all'attrezzatura e alla taglia specificate dalla repository tagliaRepo.
        Taglia tagliaCercata = tagliaRepo.findByAttrezzoAndTagliaAttrezzo(attrezzo, requestAttrezzatura.getTaglia());

        // Se la palestra non esiste, solleva un'eccezione.
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
     * @return Lista di oggetti Attrezzatura rappresentanti tutte le attrezzature.
     * @throws EntityNotFoundException Se non vengono trovate attrezzature.
     */
    public List <Attrezzatura>  getAllNoleggi() throws EntityNotFoundException{
        // Ottiene un'iterabile di tutte le attrezzature dalla repository attrezzaturaRepo.
        Iterable<Attrezzatura> attrezzi = attrezzaturaRepo.findAll();

        // lista di appoggio per memorizzare le attrezzature trovate.
        List<Attrezzatura> attrezziCercati= new ArrayList<>();

        // Itera attraverso l'iterabile e aggiunge le attrezzature alla lista.
        attrezzi.forEach(item -> attrezziCercati.add(item));

        return attrezziCercati;
    }

    /**
     * Ottiene una lista di tutte le attrezzature con noleggi non ancora terminati nel sistema.
     * @return Lista di oggetti Attrezzatura rappresentanti le attrezzature con noleggi non finiti.
     * @throws EntityNotFoundException Se non vengono trovate attrezzature.
     */

    public List<Attrezzatura> getAllNoleggiNonFiniti() throws EntityNotFoundException{
        // Ottiene un'iterabile di tutte le attrezzature dalla repository attrezzaturaRepo.
        Iterable<Attrezzatura> attrezzi = attrezzaturaRepo.findAll();

        // Lista per memorizzare i noleggi non ancora terminati.
        ArrayList<Noleggio> noleggi = new ArrayList<>();

        // Lista per memorizzare le attrezzature con noleggi non finiti.
        List<Attrezzatura> attrezziCercati= new ArrayList<>();

        // Itera attraverso le attrezzature.
        for(Attrezzatura attrezzatura:attrezzi) {
            // Itera attraverso i noleggi dell'attrezzatura.
            for (Noleggio noleggio : attrezzatura.getNoleggi()) {
                // Verifica se il noleggio non è ancora terminato e lo aggiunge alla lista.
                if (noleggio.getDataNoleggio().isAfter(LocalDateTime.now()))
                    noleggi.add(noleggio);
            }
            // operazione nelle classi di repository
            attrezzatura.setNoleggi(noleggi);
            attrezziCercati.add(attrezzatura);
        }

        return attrezziCercati;
    }


}
