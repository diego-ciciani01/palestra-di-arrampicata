package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestEscursione;
import com.polimi.palestraarrampicata.dto.request.RequestModificaEscursione;
import com.polimi.palestraarrampicata.dto.response.ResponseEscursione;
import com.polimi.palestraarrampicata.exception.CreazioneEscursioneFallita;
import com.polimi.palestraarrampicata.exception.ModificaFallita;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.Escursione;
import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.observer.EscursioneObservable;
import com.polimi.palestraarrampicata.observer.Observer;
import com.polimi.palestraarrampicata.observer.ObserverUser;
import com.polimi.palestraarrampicata.repository.EscursioniRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EscursioneService {
    private  final UtenteRepo utenteRepo;
    private final JwtUtils jwtUtils;
    private final EscursioniRepo escursioniRepo;
    private  final EscursioneObservable main;
    private  ObserverUser observerUser;

    /**
     * Crea una nuova escursione nel sistema.
     *
     * @param requestEscursione Oggetto contenente i dettagli dell'escursione da creare.
     * @return Oggetto Escursione appena creato.
     * @throws EntityNotFoundException Se l'istruttore specificato non viene trovato.
     * @throws CreazioneEscursioneFallita Se si verifica un problema durante la creazione dell'escursione.
     */
    public Escursione createEscursione(RequestEscursione requestEscursione) throws EntityNotFoundException{
        // Formatta la data di partenza dell'escursione
        LocalDateTime dataPartenza = Utils.formatterDataTime(requestEscursione.getDataPubblicazione());

        // Trova l'istruttore organizzatore dell'escursione
        Utente istruttore = utenteRepo.findUserByEmailAndRuolo(requestEscursione.getEmailOrganizzatore(), Ruolo.ISTRUTTORE)
                .orElseThrow(()->new EntityNotFoundException("L'istruttore non esiste"));

        // Controlla se la data di partenza è valida
        if(dataPartenza.isBefore(LocalDateTime.now()))
            throw new CreazioneEscursioneFallita("la data di inizio corso non può essere minore di quella di oggi");

        // Crea un nuovo oggetto Escursione e setta le informazioni
        Escursione escursione = new Escursione();
        escursione.setNomeEscursione(requestEscursione.getNomeEscursione());
        escursione.setStatoEscursione(true);
        escursione.setData(dataPartenza);
        escursione.setDescrizione(requestEscursione.getDescrizione());
        escursione.setOrganizzatore(istruttore);
        escursione.setPostiDisponibili(Integer.parseInt(requestEscursione.getPostiDisponibili()));

        // creo un osservatore con dentro un'istruttore
        observerUser = new ObserverUser(istruttore);
        main.addObserver(observerUser);
        escursioniRepo.save(escursione);

        return escursione;

    }

    /**
     * Ottiene una lista di tutte le escursioni disponibili nel sistema.
     *
     * @return Una lista di oggetti ResponseEscursione rappresentanti le escursioni disponibili.
     * @throws EntityNotFoundException Se non sono disponibili escursioni nel sistema.
     */
    public List<ResponseEscursione> getListEscursioniDisponibili()throws EntityNotFoundException{
        // Ottiene la lista di tutte le escursioni attive
        List<Escursione> escursioni = escursioniRepo.findAllByStatoEscursione(true);

        // Crea una lista per immagazzinare le risposte delle escursioni
        List<ResponseEscursione> responseEscursione = new ArrayList<>();

        // Cicla attraverso le escursioni e crea oggetti ResponseEscursione da aggiungere alla lista
        for(Escursione escursione : escursioni){
            responseEscursione.add(ResponseEscursione.builder()
                            .id(escursione.getId())
                            .nomeEscursione(escursione.getNomeEscursione())
                            .postiDisponibili(escursione.getPostiDisponibili())
                            .emailOrganizzatore(escursione.getOrganizzatore().getEmail())
                            .descrizione(escursione.getDescrizione())
                            .build());
        }

        return responseEscursione;
    }

    /**
     * Permette all'utente di iscriversi a un'escursione.
     *
     * @param id L'ID dell'escursione a cui l'utente desidera iscriversi.
     * @param httpServletRequest La richiesta HTTP per ottenere l'utente loggato.
     * @return L'oggetto Escursione a cui l'utente si è iscritto.
     * @throws RicercaFallita Se l'utente non è un utente normale o se l'escursione non è trovata.
     */
    public String partecipaEscursione(Integer id, HttpServletRequest httpServletRequest){
        // Ottiene l'utente loggato dalla richiesta HTTP
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Verifica che l'utente loggato sia un utente normale
        if(utenteLoggato.getRuolo() != Ruolo.UTENTE)
            throw new RicercaFallita("solo l'utente può iscriversi ad una escursione");

        // Ottiene l'escursione a cui l'utente desidera iscriversi
        Escursione escursione = escursioniRepo.findById(id).orElse(null);

        // Verifico se l'escursione è stata trovata
        if(escursione==null)
            throw new RicercaFallita("l'escursione cercata è inesistente");

        // Se l'escursione non ha ancora partecipanti, crea una lista di iscritti e aggiungi l'utente loggato
        if(escursione.getUtentiPartecipanti().isEmpty()){
            List<Utente> iscritti= new ArrayList<>();
            iscritti.add(utenteLoggato);
            escursione.setUtentiPartecipanti(iscritti);
        }else{
            // Se l'escursione ha già partecipanti, aggiungi l'utente loggato alla lista delle escursioni partecipate
            utenteLoggato.getEscursioniPartecipate().add(escursione);
        }

        // Salva le modifiche all'utente nel repository
        utenteRepo.save(utenteLoggato);

        observerUser = new ObserverUser(utenteLoggato);
        main.addObserver(observerUser);

        //restituisce il messaggio di avvenuta iscrizione
        return "iscrizione avvenuta correttamente";
    }

    /**
     * Elimina un'escursione dato il suo ID.
     *
     * @param idEscursione L'ID dell'escursione da eliminare.
     * @return L'oggetto ResponseEscursione corrispondente all'escursione eliminata.
     * @throws EntityNotFoundException Se l'escursione con l'ID fornito non è trovata.
     */
    public ResponseEscursione eliminaEscursione(Integer idEscursione) throws EntityNotFoundException{
        // Trova l'escursione da eliminare dato l'ID, in caso di errore sollevo un'eccezione personalizzata, RicercaFallita
        Escursione escursione = escursioniRepo.findById(idEscursione).orElseThrow(() -> new  RicercaFallita("l'escursione inserita non esiste"));

        // Elimina l'escursione dal repository
        escursioniRepo.delete(escursione);

        // Crea e restituisce un oggetto ResponseEscursione con le informazioni dell'escursione eliminata
        return ResponseEscursione.builder()
                .id(escursione.getId())
                .nomeEscursione(escursione.getNomeEscursione())
                .build();

    }
    /**
     * Ottiene una lista di escursioni organizzate da un istruttore dato il suo ID.
     *
     * @param idIstruttore L'ID dell'istruttore per il quale si vogliono ottenere le escursioni organizzate.
     * @return Una lista di oggetti ResponseEscursione che rappresentano le escursioni organizzate dall'istruttore.
     * @throws EntityNotFoundException Se l'istruttore con l'ID fornito non è trovato.
     */
    public List<ResponseEscursione> getListEscursioniByIstruttore(Integer idIstruttore){
        // Trova l'istruttore dato l'ID
        Utente utente = utenteRepo.findById(idIstruttore).orElseThrow(() -> new EntityNotFoundException("l'istruttore inserito non esiste"));

        // Ottiene la lista di escursioni organizzate dall'istruttore
        List<Escursione> escursioniIstruttore = utente.getEscursioniOrganizzate();
        List<ResponseEscursione> responseEscursione = new ArrayList<>();

        // Filtra le escursioni che devono ancora avvenire
        ArrayList<Escursione> escursioniAfternow = new ArrayList<>();
        for(Escursione e :escursioniIstruttore){
            if(e.getData().isAfter(LocalDateTime.now()))
                escursioniAfternow.add(e);
        }

        // Crea una lista di oggetti ResponseEscursione con le informazioni delle escursioni organizzate
        escursioniAfternow.forEach(cur ->{
            responseEscursione.add(ResponseEscursione.builder()
                            .id(cur.getId())
                            .nomeEscursione(cur.getNomeEscursione())
                            .data(cur.getData())
                            .emailOrganizzatore(cur.getOrganizzatore().getEmail())
                            .descrizione(cur.getDescrizione())
                            .postiDisponibili(cur.getPostiDisponibili())
                            .build());
        });
        return responseEscursione;
    }

    /**
     * Modifica un'escursione esistente con i dettagli forniti nella richiesta.
     *
     * @param requestModificaEscursione I dettagli della modifica richiesta.
     * @param httpServletRequest         La richiesta HTTP effettuata.
     * @return Una stringa che indica il successo della modifica dell'escursione.
     * @throws RicercaFallita   Se l'escursione da modificare non viene trovata.
     * @throws ModificaFallita Se l'utente non è l'organizzatore dell'escursione e non ha i permessi per apportare modifiche.
     */

    public String modificaEscursione(RequestModificaEscursione requestModificaEscursione, HttpServletRequest httpServletRequest){
        // Ottiene l'utente loggato dalla richiesta HTTP.
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Trova l'escursione con l'ID fornito o restituisce null se non trovata.
        Escursione escursione = escursioniRepo.findById( Integer.parseInt(requestModificaEscursione.getId())).orElse(null);

        // Verifica se l'escursione esiste.
        if(escursione == null)
            throw new RicercaFallita("l'escursione inserita non esiste ");
        // Verifica se l'utente loggato è l'organizzatore dell'escursione o ha i permessi per apportare modifiche.
        if(!escursione.getOrganizzatore().equals(utenteLoggato))
            throw new ModificaFallita("soltato l'isturttore che ha organizzato l'escuesione può apportare modific he");

        // Estrae i dettagli della modifica dalla richiesta.
        String numero = requestModificaEscursione.getPostiDisponibili();
        String dataEscursione = requestModificaEscursione.getData();
        String descrizione = requestModificaEscursione.getDescrizione();
        String stato =requestModificaEscursione.getStatoIscrizione();
        String nome = requestModificaEscursione.getNomeEscursione();
        escursione = new Escursione();
        // Applica le modifiche all'escursione, se i campi non sono vuoti o nulli.
        if(requestModificaEscursione.getNomeEscursione() != null && !(requestModificaEscursione.getNomeEscursione().isEmpty()))
            escursione.setNomeEscursione(nome);
        if(requestModificaEscursione.getPostiDisponibili() !=null && !(requestModificaEscursione.getPostiDisponibili().isEmpty()))
            escursione.setPostiDisponibili(Integer.parseInt(numero));
        if(requestModificaEscursione.getData() != null && !(requestModificaEscursione.getData().isEmpty()))
            escursione.setData(Utils.formatterDataTime(dataEscursione));
        if(requestModificaEscursione.getDescrizione() != null && !(requestModificaEscursione.getDescrizione().isEmpty()))
            escursione.setDescrizione(descrizione);
        if(requestModificaEscursione.getStatoIscrizione() != null && !(requestModificaEscursione.getStatoIscrizione().isEmpty()))
            escursione.setStatoEscursione(Boolean.valueOf(stato));

        // Salva le modifiche dell'escursione nel repository.
        escursioniRepo.save(escursione);

        // verifico che gli osservatori siano stati notificati
        for(Observer o: main.getObserverList()){
            o.update();
        }

        return "escursione modificata correttamente";

    }

}
