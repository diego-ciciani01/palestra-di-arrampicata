package com.polimi.palestraarrampicata.service;

import com.polimi.palestraarrampicata.dto.request.RequestAttrezzatura;
import com.polimi.palestraarrampicata.dto.request.RequestNoleggiaAttrezzatura;
import com.polimi.palestraarrampicata.dto.response.ResponseAttrezzatura;
import com.polimi.palestraarrampicata.exception.RicercaFallita;
import com.polimi.palestraarrampicata.model.Attrezzatura;
import com.polimi.palestraarrampicata.model.Palestra;
import com.polimi.palestraarrampicata.model.Taglia;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.AttrezzaturaRepo;
import com.polimi.palestraarrampicata.repository.PalestraRepo;
import com.polimi.palestraarrampicata.repository.TagliaRepo;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AttrezzaturaService {
    private final AttrezzaturaRepo attrezzaturaRepo;
    private final UtenteRepo utenteRepo;
    private final TagliaRepo tagliaRepo;
    private final PalestraRepo palestraRepo;
    private  final JwtUtils jwtUtils;

    public List<ResponseAttrezzatura> getListAttrezzaturaDisponibile(HttpServletRequest httpServletRequest) throws EntityNotFoundException{
        List<Attrezzatura> attrezzature = attrezzaturaRepo.findAllByDisponibilita(true);
        List<ResponseAttrezzatura> attrezzaturaResponse = new ArrayList<>();
        for(Attrezzatura attrezzatura: attrezzature){
            attrezzaturaResponse.add(ResponseAttrezzatura.builder()
                    .id(attrezzatura.getId().toString())
                    .nomePalestraAppartenente(attrezzatura.getAttrezziPalestra().getNome())
                    .nomeAttrezzo(attrezzatura.getNomeAttrezzatura())
                    .dataNoleggio(attrezzatura.getDataNoleggio())
                    .dataFineNoleggio(attrezzatura.getDataFineNoleggio())
                    .taglia(attrezzatura.getNomeTaglia())
                    .quantitaDisponibile(attrezzatura.getQuantita())
                    .build());
        }
        return attrezzaturaResponse;
    }
    public List<ResponseAttrezzatura> getListAttrezzaturaPerTipo(RequestAttrezzatura requestAttrezzatura){
         List <Attrezzatura> attrezzature = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
        List<ResponseAttrezzatura> attrezzaturaResponse = new ArrayList<>();
        for(Attrezzatura attrezzatura: attrezzature ){
            attrezzaturaResponse.add(ResponseAttrezzatura.builder()
                    .id(attrezzatura.getId().toString())
                    .nomePalestraAppartenente(attrezzatura.getAttrezziPalestra().getNome())
                    .nomeAttrezzo(attrezzatura.getNomeAttrezzatura())
                    .dataNoleggio(attrezzatura.getDataNoleggio())
                    .dataFineNoleggio(attrezzatura.getDataFineNoleggio())
                    .taglia(attrezzatura.getNomeTaglia())
                    .quantitaDisponibile(attrezzatura.getQuantita())
                    .build());
        }
        return attrezzaturaResponse;
    }

    public Attrezzatura noleggiaAttrazzatura(HttpServletRequest httpServletRequest, RequestNoleggiaAttrezzatura requestAttrezzatura) throws EntityNotFoundException{
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        List<Attrezzatura> attrezzature = attrezzaturaRepo.findAllByDisponibilita(true);
        LocalDateTime inizioNoleggio = Utils.formatterDataTime(requestAttrezzatura.getDataInizioNoleggio());
        LocalDateTime fineNoleggio = Utils.formatterDataTime(requestAttrezzatura.getDataFineNoleggio());
        if(!inizioNoleggio.isAfter(LocalDateTime.now()) || fineNoleggio.isBefore(inizioNoleggio))
            throw new IllegalStateException("Date inserite non valide");
        Attrezzatura nuovoAttrezzo = new Attrezzatura();
        Integer taglieDisponibili=0;
        for(Attrezzatura a : attrezzature){
            if(a.getNomeAttrezzatura().equals(requestAttrezzatura.getNomeAttrezzo())){
                for(Taglia t: a.getNomeTaglia()){
                    if(t.equals(requestAttrezzatura.getTaglia()))
                        taglieDisponibili++;
                }

                if( Integer.parseInt(requestAttrezzatura.getQuantita()) > taglieDisponibili)
                    throw new IllegalStateException("non ci sono sufficienti taglie " + requestAttrezzatura.getTaglia() + "per l'attrezzo" + requestAttrezzatura.getNomeAttrezzo());

                if( Integer.parseInt(requestAttrezzatura.getQuantita()) <= 0 || Integer.parseInt(requestAttrezzatura.getQuantita()) > a.getQuantita())
                    throw new IllegalStateException("La quantità inserita non disponibile");
                Integer rimaneze = a.getQuantita() - Integer.parseInt(requestAttrezzatura.getQuantita());

                nuovoAttrezzo.setNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
                nuovoAttrezzo.setDisponibilita((rimaneze > 0 ) ? true : false);
                nuovoAttrezzo.setQuantita(Integer.parseInt(requestAttrezzatura.getQuantita()));
                nuovoAttrezzo.setNoleggiatore(utenteLoggato);
                attrezzaturaRepo.save(nuovoAttrezzo);
            }else{
                throw new RicercaFallita("l'attrezzo cercato non è al momento presente in nessuna palestra");
            }
        }
        return nuovoAttrezzo;
    }

    public Attrezzatura inserisciNuovoAttrezzo(RequestAttrezzatura requestAttrezzatura) throws EntityNotFoundException{
        List<Attrezzatura> attrezzatura = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
        Palestra palestra = palestraRepo.findById(Integer.parseInt(requestAttrezzatura.getIdPalestraPossessore())).get();
        List<Taglia> taglia = tagliaRepo.findByNomeTaglia(requestAttrezzatura.getTaglia());

        Attrezzatura nuovoAttrezzo = new Attrezzatura();


        //situazione in cui l'attrezzo non è mai stato registrato nel sistema
        if(attrezzatura == null)
            nuovoAttrezzo.setQuantita( Integer.parseInt(requestAttrezzatura.getQuantita()));
        else {
            nuovoAttrezzo.setQuantita(attrezzatura.size() + Integer.parseInt(requestAttrezzatura.getQuantita()));
        }
        nuovoAttrezzo.setNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
        nuovoAttrezzo.setDisponibilita(true);
        nuovoAttrezzo.setAttrezziPalestra(palestra);
        if(taglia == null) {
            taglia = new ArrayList<>();
            taglia.add(new Taglia(requestAttrezzatura.getTaglia().toString(), nuovoAttrezzo));
        }
        nuovoAttrezzo.setNomeTaglia(taglia);
        attrezzaturaRepo.save(nuovoAttrezzo);

        return nuovoAttrezzo;
    }


}
