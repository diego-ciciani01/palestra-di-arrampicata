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

    /**
     * questo metodo prende una lista di tutte le attrezzature di sponibili, prende anche la quantità di
     * prodotto disponibile per ogni sigolo prodotto senza fare distinzioni di tipoologia di taglie
     * @param httpServletRequest
     * @return
     * @throws EntityNotFoundException
     */
    public List<ResponseAttrezzatura> getListAttrezzaturaDisponibile(HttpServletRequest httpServletRequest) throws EntityNotFoundException{
        List<Attrezzatura> attrezzature = attrezzaturaRepo.findAllByDisponibilita(true);
        List<ResponseAttrezzatura> attrezzaturaResponse = new ArrayList<>();
        Integer quantita=0;
        for(Attrezzatura attrezzatura: attrezzature){
            for(Taglia t : attrezzatura.getNomeTaglia()){
                quantita += t.getQuantita();
            }
            attrezzaturaResponse.add(ResponseAttrezzatura.builder()
                    .id(attrezzatura.getId().toString())
                    .nomePalestraAppartenente(attrezzatura.getAttrezziPalestra().getNome())
                    .nomeAttrezzo(attrezzatura.getNomeAttrezzatura())
                    .dataNoleggio(attrezzatura.getDataNoleggio())
                    .dataFineNoleggio(attrezzatura.getDataFineNoleggio())
                    .taglia(attrezzatura.getNomeTaglia())
                    .quantitaDisponibile(quantita)
                    .build());
            quantita = 0;
        }
        return attrezzaturaResponse;
    }
    public List<ResponseAttrezzatura> getListAttrezzaturaPerTipo(RequestAttrezzatura requestAttrezzatura){
        Attrezzatura attrezzature = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
        List<ResponseAttrezzatura> attrezzaturaResponse = new ArrayList<>();
        //Integer quantita = tagliaRepo.calcolaSommaQuantita(attrezzature.getId());
        for(Taglia taglia: attrezzature.getNomeTaglia() ){
            attrezzaturaResponse.add(ResponseAttrezzatura.builder()
                    .id(taglia.getId().toString())
                    .nomePalestraAppartenente(attrezzature.getAttrezziPalestra().getNome())
                    .nomeAttrezzo(attrezzature.getNomeAttrezzatura())
                    .dataNoleggio(attrezzature.getDataNoleggio())
                    .dataFineNoleggio(attrezzature.getDataFineNoleggio())
                    .taglia(attrezzature.getNomeTaglia())
           //         .quantitaDisponibile(quantita)
                    .build());
        }
        return attrezzaturaResponse;
    }

    public Attrezzatura noleggiaAttrazzatura(HttpServletRequest httpServletRequest, RequestNoleggiaAttrezzatura requestAttrezzatura) throws EntityNotFoundException{

        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        List<Attrezzatura> attrezzature = attrezzaturaRepo.findAllByDisponibilita(true);
        Integer quantitaPerTipologia = tagliaRepo.cercaQuantitaPerTipologiaDiTaglia(requestAttrezzatura.getNomeAttrezzo());

        LocalDateTime inizioNoleggio = Utils.formatterDataTime(requestAttrezzatura.getDataInizioNoleggio());
        LocalDateTime fineNoleggio = Utils.formatterDataTime(requestAttrezzatura.getDataFineNoleggio());
        if(!inizioNoleggio.isAfter(LocalDateTime.now()) || fineNoleggio.isBefore(inizioNoleggio))
            throw new IllegalStateException("Date inserite non valide");
        Attrezzatura nuovoAttrezzo = new Attrezzatura();
        Integer taglieDisponibili=0;
        for(Attrezzatura a : attrezzature){
            if(a.getNomeAttrezzatura().equals(requestAttrezzatura.getNomeAttrezzo())){
                Taglia tagliaCercata = tagliaRepo.findByIdAndTagliaAttrezzo(a.getId(), requestAttrezzatura.getTaglia()).get();
                if( Integer.parseInt(requestAttrezzatura.getQuantita()) > tagliaCercata.getQuantita() )
                    throw new IllegalStateException("non ci sono sufficienti taglie " + requestAttrezzatura.getTaglia() + "per l'attrezzo" + requestAttrezzatura.getNomeAttrezzo());

                if( Integer.parseInt(requestAttrezzatura.getQuantita()) <= 0 || Integer.parseInt(requestAttrezzatura.getQuantita()) > tagliaCercata.getQuantita())
                    throw new IllegalStateException("La quantità inserita non disponibile");

                Integer rimaneze = quantitaPerTipologia - Integer.parseInt(requestAttrezzatura.getQuantita());

                nuovoAttrezzo.setNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
                nuovoAttrezzo.setDisponibilita((rimaneze > 0 ) ? true : false);
                tagliaCercata.setQuantita(rimaneze);
                nuovoAttrezzo.setNoleggiatore(utenteLoggato);
                tagliaRepo.save(tagliaCercata);
                attrezzaturaRepo.save(nuovoAttrezzo);
            }else{
                throw new RicercaFallita("l'attrezzo cercato non è al momento presente in nessuna palestra");
            }
        }

        return nuovoAttrezzo;
    }

    public Attrezzatura inserisciNuovoAttrezzo(RequestAttrezzatura requestAttrezzatura) throws EntityNotFoundException{
        Attrezzatura attrezzo = attrezzaturaRepo.findByNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
        Palestra palestra = palestraRepo.findById(Integer.parseInt(requestAttrezzatura.getIdPalestraPossessore())).get();
        //Taglia tagliaCercata = tagliaRepo.findByIdAndTaglia(attrezzo.getId(), requestAttrezzatura.getTaglia()).get();
        Iterable<Taglia> taglieList = tagliaRepo.findAll();
        List<Taglia> taglieCercate  = new ArrayList<>();
        for(Taglia t: taglieList) {
            if(t.getId().equals(attrezzo.getId()) && t.getTagliaAttrezzo().equals(requestAttrezzatura.getTaglia()))
                taglieCercate.add(t);
        }

        Attrezzatura nuovoAttrezzo = new Attrezzatura();
        if(attrezzo ==null && taglieCercate.isEmpty()){
            Taglia taglia = new Taglia(requestAttrezzatura.getTaglia().toString(), nuovoAttrezzo,Integer.parseInt(requestAttrezzatura.getQuantita()));
            taglieCercate.add(taglia);

            //situazione in cui l'attrezzo non è mai stato registrato nel sistema
            nuovoAttrezzo.setNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
            nuovoAttrezzo.setDisponibilita(true);
            nuovoAttrezzo.setAttrezziPalestra(palestra);
            taglia.setAttrezzo(nuovoAttrezzo);
            nuovoAttrezzo.setNomeTaglia(taglieCercate);
            attrezzaturaRepo.save(nuovoAttrezzo);
            tagliaRepo.save(taglia);
        }
        return nuovoAttrezzo;


        /*


        if(taglie.isEmpty() && attrezzo == null) {
            Taglia taglia = new Taglia(requestAttrezzatura.getTaglia().toString(), nuovoAttrezzo,Integer.parseInt(requestAttrezzatura.getQuantita()));
            taglie = new ArrayList<>();
            taglie.add(taglia);

            //situazione in cui l'attrezzo non è mai stato registrato nel sistema
            nuovoAttrezzo.setNomeAttrezzatura(requestAttrezzatura.getNomeAttrezzo());
            nuovoAttrezzo.setDisponibilita(true);
            nuovoAttrezzo.setAttrezziPalestra(palestra);
            taglia.setAttrezzo(nuovoAttrezzo);
            attrezzaturaRepo.save(nuovoAttrezzo);
            tagliaRepo.save(taglia);
            return nuovoAttrezzo;

        }else if(taglie.isEmpty() && attrezzo != null){
            Taglia taglia = new Taglia(requestAttrezzatura.getTaglia().toString(), nuovoAttrezzo,Integer.parseInt(requestAttrezzatura.getQuantita()));
            taglie = new ArrayList<>();
            taglie.add(taglia);
            taglia.setAttrezzo(attrezzo);
            attrezzaturaRepo.save(attrezzo);
            tagliaRepo.save(taglia);
            return attrezzo;

        }else if(attrezzo != null && !(taglie.isEmpty())){
            //SITUAZIONE IN CUI SIA L'ATTREZZO CHE LA TAGLIA ESISTONO
            taglie.add();
        }

            attrezzo.setDisponibilita(true);
            attrezzo.setNomeTaglia(taglie);
            attrezzaturaRepo.save(attrezzo);
            return attrezzo;
    */
   }


}
