package com.polimi.palestraarrampicata.service;


import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.request.RequestValutazione;
import com.polimi.palestraarrampicata.dto.response.ResponseCommento;
import com.polimi.palestraarrampicata.dto.response.ResponseLezione;
import com.polimi.palestraarrampicata.exception.CreazioneCommentoFallita;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.*;
import com.polimi.palestraarrampicata.repository.CommentoRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtenteService implements UserDetailsService {

    private final UtenteRepo utenteRepo;
    private final CommentoRepo commentoRepo;
    private  final JwtUtils jwtUtils;
    @Override
    public UserDetails loadUserByUsername(String email) throws IllegalStateException {
        return utenteRepo.findUserByEmail(email).orElseThrow(()-> new IllegalStateException("l'utente non è stato trovato"));
    }

    /*
    public Utente modificaUtente(RequestModificaUtente requestModifica, HttpServletRequest request){
        Utente utenteLoggato = Utils.getUtenteFromHeader(request, utenteRepo);
        String oldPassword = utenteLoggato.getPassword();
        boolean usernameModificato = false;

        String nome = requestModifica.getNome();
        String cognome = requestModifica.getCognome();
        String username = requestModifica.getUsername();
        String email = requestModifica.getEmail();
        String password = requestModifica.getPassword();
        String fotoProfilo = requestModifica.getFotoProfilo();

        if(utenteRepo.findByUsernameOrEmail(username, email) != null){
            throw new ModificaFallita("Username o email già in uso");
        }
        if(nome != null)
            utenteLoggato.setNome(nome);
        if(cognome != null)
            utenteLoggato.setCognome(cognome);
        if(username != null)
            utenteLoggato.setUsername(username);
        if(email != null)
            utenteLoggato.setUsername(email);
        if(password != null && !password.equals(oldPassword)) {
            utenteLoggato.setPassword(password);
            usernameModificato = true;
        }
        if (fotoProfilo != null){
            byte[] fotoProfiloByte = Base64.getDecoder().decode(fotoProfilo.getBytes(StandardCharsets.UTF_8));
            utenteLoggato.setFotoProfilo(fotoProfiloByte);
        }
        utenteRepo.save(utenteLoggato);
        return usernameModificato;

    }
*/
    public List<ResponseLezione> getListInvitiLezione(HttpServletRequest httpServletRequest) throws EntityNotFoundException{

        List<ResponseLezione> lezioneList = new ArrayList<>();
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        if(utenteLoggato.getRuolo() != Ruolo.UTENTE)
            throw new RicercaFallita("Utenete inserito non valido");
        for(Lezione l: utenteLoggato.getInviti()){
            lezioneList.add(ResponseLezione
                    .builder()
                    .id(l.getId().toString())
                    .dataLezione(l.getData().toString())
                    .statoLezione(l.getStatoLezione())
                    .istruttore(l.getIstruttore().getEmail().toString())
                    .build());
        }
        return lezioneList;
    }

    public List<ResponseLezione> getListInvitiLezioneAccettate(HttpServletRequest httpServletRequest) throws EntityNotFoundException {
        List<ResponseLezione> lezioneList = new ArrayList<>();
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        if(utenteLoggato.getRuolo() != Ruolo.UTENTE)
            throw new RicercaFallita("Utenete inserito non valido");
        for(Lezione l: utenteLoggato.getInviti()){
            if(l.getStatoLezione()) {
                lezioneList.add(ResponseLezione
                        .builder()
                        .id(l.getId().toString())
                        .dataLezione(l.getData().toString())
                        .statoLezione(l.getStatoLezione())
                        .istruttore(l.getIstruttore().getEmail().toString())
                        .build());
            }
        }
        return lezioneList;
    }
    public String deleteUserByEmail(String email){
        if(email.isEmpty()) throw new IllegalStateException("Email non esistente");
            Utente user = (Utente) loadUserByUsername(email);
            utenteRepo.delete(user);
            return "L'utente" + user.getEmail() + "è stato eliminato correttamente";
    }

    public Commento creaCommento(HttpServletRequest httpServletRequest, RequestCommento requestCommento){
        try{
            Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest,utenteRepo ,jwtUtils );
            LocalDateTime dataPubblicazione = Utils.withoutSeconds(LocalDateTime.now());
            //Integer idIstruttoreCommentato = Integer.parseInt(requestCommento.getEmailIstruttoreCommentato());
            Utente  istruttore =  utenteRepo.findByEmail(requestCommento.getEmailIstruttoreCommentato());
            Integer idIstruttoreCommentato = istruttore.getId();
            Commento commentoNuovo = new Commento();

            Commento commentoPadre = null;
            Integer idCommentoPadre = null;

            if(requestCommento.getIdCommentoPadre() != null) {
                idCommentoPadre = Integer.parseInt(requestCommento.getIdCommentoPadre());

                if (idCommentoPadre != null)
                    commentoPadre = commentoRepo.findById(idCommentoPadre).orElseThrow(() -> new CreazioneCommentoFallita("il commento padre con l'id fornito non esiste"));

                if (!commentoPadre.getIstruttoreCommentato().getId().equals(istruttore.getId()))
                    throw new CreazioneCommentoFallita("L'istruttore commentato a quella del commento padre");
            }
            commentoNuovo.setCommentoPadre(commentoPadre);
            commentoNuovo.setCommentatore(utenteLoggato);
            commentoNuovo.setIstruttoreCommentato(istruttore);
            commentoNuovo.setTesto(requestCommento.getTesto());
            commentoNuovo.setDataInserimento(dataPubblicazione);
            commentoRepo.save(commentoNuovo);
            return commentoNuovo;
        }catch (DateTimeParseException e){
            throw new CreazioneCommentoFallita("Formato data inserimento non valida");
        }catch (IllegalArgumentException e){
            throw new CreazioneCommentoFallita("Parametri errati");
        }

    }
    public Valutazione creaValutazione(HttpServletRequest httpServletRequest, RequestValutazione requestValutazione){

    }



}
