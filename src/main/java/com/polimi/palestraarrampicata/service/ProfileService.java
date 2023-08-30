package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestLogin;
import com.polimi.palestraarrampicata.dto.request.RequestModificaUtente;
import com.polimi.palestraarrampicata.dto.request.RequestRegistrazione;
import com.polimi.palestraarrampicata.dto.response.AuthenticationResponse;
import com.polimi.palestraarrampicata.exception.LogoutFallito;
import com.polimi.palestraarrampicata.exception.ModificaFallita;
import com.polimi.palestraarrampicata.exception.RegistrazioneFallita;
import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final JwtUtils jwtUtils;
    private final UtenteRepo utenteRepo;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuovo utente nel sistema.
     *
     * @param requestRegistrazione Contiene le informazioni necessarie per la registrazione dell'utente.
     * @return L'oggetto Utente appena registrato.
     * @throws RegistrazioneFallita Se si verifica un errore durante il processo di registrazione.
     */
    public Utente registrazione(RequestRegistrazione requestRegistrazione){
        Utente utente = new Utente();

        try{
            String username = requestRegistrazione.getEmail();
            // Verifica se l'username o l'email sono già in uso
            if (utenteRepo.findUserByEmail(username).isPresent())
                throw new RegistrazioneFallita("Username o Email già in uso");

            // Verifica se l'email rispetta il formato corretto
            Pattern pattern = Pattern.compile(Utils.REGEX_EMAIL);
            Matcher matcher = pattern.matcher(requestRegistrazione.getEmail());
            if(matcher.matches() == false) throw new IllegalStateException("l'email inserita non è corretta");


            // Imposta le informazioni dell'utente
            utente.setNome(requestRegistrazione.getNome());
            utente.setCognome(requestRegistrazione.getCognome());
            utente.setEmail(requestRegistrazione.getEmail());
            // della password viene fatto l'encoder, in modo di rendere più sicura la password nel database
            // così anche occhi indiscreti non potranno vedere il valore in chiaro nella tabella del DB
            utente.setPassword(passwordEncoder.encode(requestRegistrazione.getPassword()));
            Ruolo ruolo = Ruolo.valueOf(requestRegistrazione.getRuolo().toUpperCase());
            utente.setRuolo(ruolo);
            LocalDate dateNascita= Utils.parseLocalDateFromString(requestRegistrazione.getDataNascita());
            utente.setDataDiNascita(dateNascita);
            utente.setEnable(true);
            String fotoProfilo = requestRegistrazione.getFotoProfilo();


            if(fotoProfilo != null){
                if(fotoProfilo.isBlank()) {
                    throw new RegistrazioneFallita("La foto profilo, se inserita, non può essere vuota");
                }
                byte[] fotoProfiloBytes = Base64.getDecoder().decode(fotoProfilo.getBytes(StandardCharsets.UTF_8));
                utente.setFotoProfilo(fotoProfiloBytes);
            }

            // Salva l'utente nel repository
            utenteRepo.save(utente);

        }catch (IllegalArgumentException e){
            throw  new RegistrazioneFallita("il ruolo inserito non è valido");
        }
        return  utente;
    }

    /**
     * Gestisce l'eccezione HttpMediaTypeNotAcceptableException e gestisce il processo di login dell'utente.
     *
     * @param requestLogin Oggetto contenente le credenziali di accesso dell'utente.
     * @return Il token JWT generato in seguito al login dell'utente.
     * @throws IllegalStateException Se si verifica un errore durante l'autenticazione.
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public String login(RequestLogin requestLogin){
        UserDetails user;
        Authentication auth;
        //Controlla se l'utente con queste credenziali esiste nel SecurityContext
        try {
            // Effettua l'autenticazione dell'utente utilizzando le credenziali fornite
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestLogin.getEmail(), requestLogin.getPassword())
            );
        }catch(AuthenticationException e) {
            // Se l'autenticazione fallisce, solleva un'eccezione
            throw new IllegalStateException(e.getMessage());
        }
        //Prende le credenziali dell'utente che sta facendo l'autenticazione
        user = (UserDetails) auth.getPrincipal();

        // Crea una mappa vuota per le claims del token JWT
        Map<String, Object> claims = new HashMap<>();

        //Genera il token associato all'utente con una durata di 24 ore
        String jwtToken = jwtUtils.generateToken(
                user,
                claims

        );
        // Restituisce il token JWT generato
        return  jwtToken;
    }

    /**
     * Modifica le informazioni dell'utente loggato nel sistema.
     *
     * @param requestModifica Le nuove informazioni dell'utente da modificare.
     * @param request HttpServletRequest per ottenere l'utente loggato dalla richiesta.
     * @return Un messaggio di conferma sulla modifica dell'utente.
     * @throws ModificaFallita Se si verifica un errore durante il processo di modifica.
     */
    public String modificaUtente(RequestModificaUtente requestModifica, HttpServletRequest request){
        // Ottieni l'utente loggato dalla richiesta
        Utente utenteLoggato = Utils.getUserFromHeader(request,utenteRepo ,jwtUtils );

        // Salva la password attuale dell'utente
        String oldPassword = utenteLoggato.getPassword();

        // Estrai le nuove informazioni dall'oggetto RequestModificaUtente
        String nome = requestModifica.getNome();
        String cognome = requestModifica.getCognome();
        String email = requestModifica.getEmail();
        String password = requestModifica.getPassword();
        String fotoProfilo = requestModifica.getFotoProfilo();

        // Verifica se l'email è già in uso da un altro utente
        if(utenteRepo.findByEmail(email) != null){
            throw new ModificaFallita("Username o email già in uso");
        }

        // Applica le modifiche se le nuove informazioni sono state fornite
        if(nome != null)
            utenteLoggato.setNome(nome);
        if(cognome != null)
            utenteLoggato.setCognome(cognome);
        if(email != null)
            utenteLoggato.setEmail(email);
        if(password != null && !password.equals(oldPassword)) {
            // Se è stata fornita una nuova password diversa da quella attuale
            utenteLoggato.setPassword(password);

        }
        if (fotoProfilo != null){
            // Decodifica e salva la nuova foto profilo in formato byte
            byte[] fotoProfiloByte = Base64.getDecoder().decode(fotoProfilo.getBytes(StandardCharsets.UTF_8));
            utenteLoggato.setFotoProfilo(fotoProfiloByte);
        }
        // Salva le modifiche nell'utenteRepository
        utenteRepo.save(utenteLoggato);
        return "utente modificato correttamente";

    }

    /**
     * Esegue la procedura di logout per l'utente corrente invalidando il token JWT.
     *
     * @param request HttpServletRequest per ottenere il token di autenticazione dalla richiesta.
     * @throws LogoutFallito Se si verifica un errore durante il processo di logout.
     */
    public void logout(HttpServletRequest request) {
        // Ottieni il token JWT dall'header "Authorization"
        String token = request.getHeader("Authorization").substring(7);

        // Invalida il token aggiungendolo alla blacklist
        boolean logoutEffettuato = jwtUtils.blacklistToken(token);

        // Verifica se il logout è stato effettuato correttamente
        if(!logoutEffettuato)
            throw new LogoutFallito("Logout fallito: token già presente nella blacklist");
    }


}
