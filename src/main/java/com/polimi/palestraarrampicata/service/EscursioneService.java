package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestEscursione;
import com.polimi.palestraarrampicata.dto.response.ResponseEscursione;
import com.polimi.palestraarrampicata.exception.CreazioneEscursioneFallita;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.Escursione;
import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.Utente;
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

    public Escursione createEscursione(RequestEscursione requestEscursione, HttpServletRequest httpServletRequest) throws EntityNotFoundException{
        LocalDateTime dataPartenza = Utils.formatterDataTime(requestEscursione.getDataPubblicazione());
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);

        Utente istruttore = utenteRepo.findUserByEmailAndRuolo(requestEscursione.getEmailOrganizzatore(), Ruolo.ISTRUTTORE)
                .orElseThrow(()->new EntityNotFoundException("L'istruttore non esiste"));

        if(dataPartenza.isBefore(LocalDateTime.now()))
            throw new CreazioneEscursioneFallita("la data di inizio corso non può essere minore di quella di oggi");

        Escursione escursione = new Escursione();
        escursione.setNomeEscursione(requestEscursione.getNomeEscursione());
        escursione.setStatoEscursione(true);
        escursione.setData(dataPartenza);
        escursione.setDescrizione(requestEscursione.getDescrizione());
        escursione.setOrganizzatore(istruttore);
        escursione.setPostiDisponibili(Integer.parseInt(requestEscursione.getPostiDisponibili()));

        return escursione;
    }

    public List<ResponseEscursione> getListEscursioniDisponibili()throws EntityNotFoundException{
        List<Escursione> escursioni = escursioniRepo.findAllByStatoEscursione(true);
        List<ResponseEscursione> responseEscursione = new ArrayList<>();

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

    public Escursione partecipaEscursione(Integer id, HttpServletRequest httpServletRequest){
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        if(utenteLoggato.getRuolo() != Ruolo.UTENTE)
            throw new RicercaFallita("solo l'utente può iscriversi ad una escursione");

        Escursione escursione = escursioniRepo.findById(id).orElse(null);
        if(escursione==null)
            throw new RicercaFallita("l'escursione cercata è inesistente");

        if(escursione.getUtentiPartecipanti().isEmpty()){
            List<Utente> iscritti= new ArrayList<>();
            iscritti.add(utenteLoggato);
            escursione.setUtentiPartecipanti(iscritti);
        }else{
            utenteLoggato.getEscursioniPartecipate().add(escursione);
        }
        utenteRepo.save(utenteLoggato);

        return escursione;
    }

    public ResponseEscursione eliminaEscursione(Integer idEscursione) throws EntityNotFoundException{
        Escursione escursione = escursioniRepo.findById(idEscursione).orElseThrow(() -> new  RicercaFallita("l'escursione inserita non esiste"));
        escursioniRepo.delete(escursione);
        return ResponseEscursione.builder()
                .id(escursione.getId())
                .nomeEscursione(escursione.getNomeEscursione())
                .build();

    }

    public List<ResponseEscursione> getListEscursioniByIstruttore(Integer idIstruttore){
        Utente utente = utenteRepo.findById(idIstruttore).orElseThrow(() -> new EntityNotFoundException("l'istruttore inserito non esiste"));
        List<Escursione> escursioniIstruttore = utente.getEscursioniOrganizzate();
        List<ResponseEscursione> responseEscursione = new ArrayList<>();

        ArrayList<Escursione> escursioniAfternow = new ArrayList<>();

        for(Escursione e :escursioniIstruttore){
            if(e.getData().isAfter(LocalDateTime.now()))
                escursioniAfternow.add(e);
        }
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

}
