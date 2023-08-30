package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestIscrivitiPalestra;
import com.polimi.palestraarrampicata.dto.request.RequestPalestra;
import com.polimi.palestraarrampicata.dto.response.ResponsePalestra;
import com.polimi.palestraarrampicata.dto.response.ResponseUtente;
import com.polimi.palestraarrampicata.model.Palestra;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.PalestraRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
@AllArgsConstructor
public class PalestraService {

    private final PalestraRepo palestraRepo;
    private final UtenteRepo utenteRepo;
    private final JwtUtils jwtUtils;

    /**
     * Crea una nuova palestra utilizzando i dati forniti nella richiesta e restituisce l'oggetto Palestra creato.
     *
     * @param requestPalestra La richiesta contenente i dati per la creazione della palestra.
     * @return L'oggetto Palestra creato con successo.
     * @throws EntityNotFoundException Se si verifica un'eccezione EntityNotFoundException durante la creazione.
     * @throws IllegalStateException Se la palestra è già presente o se ci sono problemi con i dati forniti (es. CAP errato, numero di telefono non valido).
     */
    public Palestra createPalestra(RequestPalestra requestPalestra) throws EntityNotFoundException{
        // cerco la palestra all'interno del db
        Optional<Palestra> palestra = palestraRepo.findByNomeAndIndirizzo(requestPalestra.getNomePalestra(), requestPalestra.getIndirizzo());

        // controllo se la palestra che si vuole creare è gia presente o meno
        if(palestra.isPresent()) throw new IllegalStateException("Palestra già presente");

        // effettuo controlli sulla dimensione del cap inserito, deve essere dimensione 5
        if(requestPalestra.getCap().length() != 5) throw new IllegalStateException("il cap deve essere di 5 numeri");
        Pattern pattern = Pattern.compile(Utils.REGEX_TELEFONO);
        Matcher matcher = pattern.matcher(requestPalestra.getTelefono());
        if(matcher.matches() == false) throw new IllegalStateException("il numero di telefono inserito non rispetta il pattern dei numeri italiani");

        // Crea una nuova istanza di Palestra utilizzando i dati forniti nella richiesta
        Palestra newPalestra = new Palestra(
                requestPalestra.getCap(),
                requestPalestra.getCitta(),
                requestPalestra.getTelefono(),
                requestPalestra.getIndirizzo(),
                requestPalestra.getNomePalestra(),
                requestPalestra.getEmailPalestra()
        );
        // Salva la nuova palestra nel database
        palestraRepo.save(newPalestra);

        // Restituisce l'oggetto Palestra appena creato
        return newPalestra;
    }


    /**
     * Ottiene tutte le palestre presenti nel sistema e restituisce una lista di oggetti ResponsePalestra.
     *
     * @return Una lista di oggetti ResponsePalestra contenenti le informazioni di tutte le palestre presenti nel sistema.
     * @throws EntityNotFoundException Se si verifica un'eccezione EntityNotFoundException durante l'ottenimento delle palestre.
     */
    public List<ResponsePalestra> getAllPalestre(){
        // Ottiene tutte le palestre dal repository
        Iterable<Palestra> palestre = palestraRepo.findAll();

        // Crea una lista per contenere le risposte delle palestre
        List<ResponsePalestra> palestreResponse = new ArrayList<>();

        // Itera attraverso le palestre ottenute e crea oggetti ResponsePalestra per ciascuna
        for(Palestra p: palestre){
            palestreResponse.add(ResponsePalestra.builder()
                            .nome(p.getNome())
                            .id(p.getId().toString())
                            .build());
        }
        // Restituisce la lista di oggetti ResponsePalestra
        return palestreResponse;
    }

    /**
     * Rimuove l'iscrizione di un utente dalla palestra.
     *
     * @param email L'email dell'utente da disiscrivere.
     * @return Un messaggio indicante che l'utente è stato disiscritto correttamente.
     * @throws EntityNotFoundException Se l'email non appartiene a nessun utente nel sistema.
     */
    public String disiscriviUtente(String email)throws EntityNotFoundException{
        // Trova l'utente corrispondente all'email fornita, se non trovato solleva una EntityNotFound Exception
        Utente utenteDaDisiscrivere = utenteRepo.findUserByEmail(email).orElseThrow(() -> new EntityNotFoundException("l'email inserita non appartiene a nessun utente"));

        // Ottiene la palestra a cui l'utente è iscritto
        Palestra palestra = utenteDaDisiscrivere.getIscrittiPalestra();

        // Rimuove l'associazione dell'utente con la palestra
        utenteDaDisiscrivere.setIscrittiPalestra(null);

        // Ottiene la lista degli utenti iscritti alla palestra
        List<Utente> utentiIscritti = palestra.getIscrittiPalestra();

        // Rimuove l'utente dalla lista degli utenti iscritti alla palestra
        utentiIscritti.remove(utentiIscritti);

        // Salva le modifiche nell'utente e nella palestra
        utenteRepo.save(utenteDaDisiscrivere);
        palestraRepo.save(palestra);

        // Restituisce messaggio di conferma
        return "utente " + email + " disiscritto correttamente";

    }

    /**
     * Iscrive un utente a una palestra.
     *
     * @param httpServletRequest La richiesta HTTP in cui è contenuto l'header dell'utente autenticato.
     * @param requestIscrivitiPalestraPalestra I dettagli della palestra a cui iscrivere l'utente.
     * @return Un messaggio che conferma l'iscrizione dell'utente alla palestra.
     * @throws EntityNotFoundException Se la palestra specificata non esiste.
     */
    public String iscriviUtentePalestra(HttpServletRequest httpServletRequest, RequestIscrivitiPalestra requestIscrivitiPalestraPalestra){
        // Ottiene l'utente autenticato dalla richiesta
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        // Trova la palestra corrispondente al nome e all'email forniti, se non trovata solleva una EntityNotFound Exception
        Palestra palestaDaIscrivere = palestraRepo.findByNomeAndEmailPalestra(requestIscrivitiPalestraPalestra.getNomePalestra(), requestIscrivitiPalestraPalestra.getEmailPalestra())
                .orElseThrow(() ->new EntityNotFoundException("la palestra cercata non esiste"));

        // Verifica se la palestra ha già iscritti
        if(palestaDaIscrivere.getIscrittiPalestra().isEmpty()){
            // Se la palestra non ha ancora iscritti, crea una lista con l'utente corrente e la assegna alla palestra
            List<Utente> iscritti = new ArrayList<>();
            iscritti.add(utenteLoggato);
            palestaDaIscrivere.setIscrittiPalestra(iscritti);

            // Assegna la palestra all'utente
            utenteLoggato.setIscrittiPalestra(palestaDaIscrivere);
        }else {
            // Se la palestra ha già iscritti, aggiunge l'utente corrente alla lista degli iscritti
            palestaDaIscrivere.getIscrittiPalestra().add(utenteLoggato);
        }
        // Salva le modifiche nell'utente e nella palestra
        utenteRepo.save(utenteLoggato);
        palestraRepo.save(palestaDaIscrivere);

        // Restituisce un messaggio di conferma
        return "utente "+ utenteLoggato.getEmail() +" iscirtto correttamente alla palestra " + palestaDaIscrivere.getNome();
    }

    /**
     * Ottiene la lista di utenti iscritti a una palestra in base all'indirizzo email della palestra.
     *
     * @param emailPalestra L'indirizzo email della palestra di cui ottenere gli utenti iscritti.
     * @return Una lista di utenti iscritti alla palestra.
     * @throws EntityNotFoundException Se la palestra con l'indirizzo email specificato non esiste.
     */
    public List<Utente> getAllIscrittiBYEmailPalestra(String emailPalestra){
        // Trova la palestra corrispondente all'indirizzo email fornito, se non trovato solleva una EntityNotFound Exception
        Palestra palestraCercata = palestraRepo.findByEmailPalestra(emailPalestra) .orElseThrow(() ->new EntityNotFoundException("la palestra cercata non esiste"));;

        // Crea una lista per gli utenti iscritti
        List<Utente> utentiIscritti = new ArrayList<>();

        // Aggiunge tutti gli utenti iscritti alla lista
        palestraCercata.getIscrittiPalestra().forEach(utente -> utentiIscritti.add(utente));

        return utentiIscritti;
    }
}
