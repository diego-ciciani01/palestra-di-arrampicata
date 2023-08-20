package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestEscursione;
import com.polimi.palestraarrampicata.exception.CreazioneEscursioneFallita;
import com.polimi.palestraarrampicata.model.Escursione;
import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class EscursioneService {
    private  final UtenteRepo utenteRepo;
    private final JwtUtils jwtUtils;
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

}
