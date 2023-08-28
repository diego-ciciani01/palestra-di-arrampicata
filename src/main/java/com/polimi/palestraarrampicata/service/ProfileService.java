package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestLogin;
import com.polimi.palestraarrampicata.dto.request.RequestModificaUtente;
import com.polimi.palestraarrampicata.dto.request.RequestRegistrazione;
import com.polimi.palestraarrampicata.dto.response.AuthenticationResponse;
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


    public Utente registrazione(RequestRegistrazione requestRegistrazione){
        Utente utente = new Utente();

        try{
            String username = requestRegistrazione.getEmail();

            if (utenteRepo.findUserByEmail(username).isPresent())
                throw new RegistrazioneFallita("Username o Email già in uso");

            Pattern pattern = Pattern.compile(Utils.REGEX_EMAIL);
            Matcher matcher = pattern.matcher(requestRegistrazione.getEmail());
            if(matcher.matches() == false) throw new IllegalStateException("l'email inserita non è corretta");

            utente.setNome(requestRegistrazione.getNome());
            utente.setCognome(requestRegistrazione.getCognome());
            utente.setEmail(requestRegistrazione.getEmail());

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
            utenteRepo.save(utente);

        }catch (IllegalArgumentException e){
            throw  new RegistrazioneFallita("il ruolo inserito non è valido");
        }
        return  utente;
    }
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public String login(RequestLogin requestLogin){
        UserDetails user;
        Authentication auth;
        //Controlla se l'utente con queste credenziali esiste nel SecurityContext
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestLogin.getEmail(), requestLogin.getPassword())
            );
        }catch(AuthenticationException e) {
            throw new IllegalStateException(e.getMessage());
        }
        //Prende le credenziali dell'utente che sta facendo l'autenticazione
        user = (UserDetails) auth.getPrincipal();
        Map<String, Object> claims = new HashMap<>();

        //Genera il token associato all'utente con una durata di 24 ore
        String jwtToken = jwtUtils.generateToken(
                user,
                claims

        );

        return  jwtToken;
    }


    public String modificaUtente(RequestModificaUtente requestModifica, HttpServletRequest request){

        Utente utenteLoggato = Utils.getUserFromHeader(request,utenteRepo ,jwtUtils );
        String oldPassword = utenteLoggato.getPassword();
        boolean usernameModificato = false;

        String nome = requestModifica.getNome();
        String cognome = requestModifica.getCognome();
        String email = requestModifica.getEmail();
        String password = requestModifica.getPassword();
        String fotoProfilo = requestModifica.getFotoProfilo();

        if(utenteRepo.findByEmail(email) != null){
            throw new ModificaFallita("Username o email già in uso");
        }
        if(nome != null)
            utenteLoggato.setNome(nome);
        if(cognome != null)
            utenteLoggato.setCognome(cognome);
        if(email != null)
            utenteLoggato.setEmail(email);
        if(password != null && !password.equals(oldPassword)) {
            utenteLoggato.setPassword(password);
            usernameModificato = true;
        }
        if (fotoProfilo != null){
            byte[] fotoProfiloByte = Base64.getDecoder().decode(fotoProfilo.getBytes(StandardCharsets.UTF_8));
            utenteLoggato.setFotoProfilo(fotoProfiloByte);
        }
        utenteRepo.save(utenteLoggato);
        return "utente modificato correttamente";

    }


}
