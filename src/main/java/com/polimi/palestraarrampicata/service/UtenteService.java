package com.polimi.palestraarrampicata.service;


import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.request.RequestValutazione;
import com.polimi.palestraarrampicata.dto.response.ResponseCommento;
import com.polimi.palestraarrampicata.dto.response.ResponseLezione;
import com.polimi.palestraarrampicata.exception.CreazioneCommentoFallita;
import com.polimi.palestraarrampicata.exception.InserimentoValutazioneFallita;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.*;
import com.polimi.palestraarrampicata.repository.CommentoRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.repository.ValutazioneRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

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
    private  final ValutazioneRepo valutazioneRepo;
    private  final JwtUtils jwtUtils;
    @Override
    public UserDetails loadUserByUsername(String email) throws IllegalStateException {
        return utenteRepo.findUserByEmail(email).orElseThrow(()-> new IllegalStateException("l'utente non è stato trovato"));
    }

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
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest,utenteRepo ,jwtUtils );
        Utente istruttoreDaValutare = null;
        istruttoreDaValutare=  utenteRepo.findByEmail(requestValutazione.getEmailValutato());

        if(istruttoreDaValutare == null){
            throw new InserimentoValutazioneFallita("Utente da valutare inserito non esistente");
        }else {
            Valutazione valutazione = valutazioneRepo.findByValutatoreAndValutato(utenteLoggato, istruttoreDaValutare);
            if(valutazione == null){
                Valutazione nuovaValutazione = new Valutazione();
                nuovaValutazione.setValore(Integer.parseInt(requestValutazione.getValore()));
                nuovaValutazione.setValutatore(utenteLoggato);
                nuovaValutazione.setValutato(istruttoreDaValutare);
                valutazioneRepo.save(nuovaValutazione);
                return  nuovaValutazione;
            }else{
                throw new InserimentoValutazioneFallita("la valutazione è già stata inserita per questo istruttore");
            }
        }
    }

    /**
     * Con questo metodo andiamo a prendere una lista di commenti sotto l'id istruttore passato nel
     * metodo, se l'utente loggato ha partecipato alla conversazione dei commenti, ritorniamo la conversazione
     * @param httpServletRequest
     * @param idIstruttore
     * @return
     */
    public List <ResponseCommento> getListCommentifromUtenteToIstruttore(HttpServletRequest httpServletRequest, Integer idIstruttore){
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        Utente istruttore = utenteRepo.findById(idIstruttore).orElseThrow(() -> new EntityNotFoundException("l'istruttore inserito non esiste"));
        List<Commento> commentiUtente = commentoRepo.findAllByCommentatoreAndIstruttoreCommentato(utenteLoggato, istruttore);
        if(commentiUtente==null)
            throw new EntityNotFoundException("non ci sono commenti fatti verso l'istuttore " + istruttore.getEmail());

        List<ResponseCommento> responseCommenti = new ArrayList<>();
        for(Commento c: commentiUtente){
            responseCommenti.add(ResponseCommento.builder()
                            .emailIstruttore(istruttore.getEmail())
                            .testo(c.getTesto())
                            .commentatore(c.getCommentatore().getEmail())
                            .build());
        }
        return responseCommenti;

    }
    /*
    public String getAvgValutazione(String email){
        Utente istruttore = utenteRepo.findUserByEmailAndRuolo(email, Ruolo.ISTRUTTORE).orElseThrow(() -> new EntityNotFoundException("l'istruttore inserito non esiste"));
        double avg = valutazioneRepo.getValutazioneOrganizzatore(istruttore.getId());

        return "la media delle valutazione dell'istruttore "+ istruttore.getEmail() +" è "+ Double.toString(avg);

    }
     */

}
