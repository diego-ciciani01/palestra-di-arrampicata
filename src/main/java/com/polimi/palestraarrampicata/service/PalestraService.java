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
    public Palestra createPalestra(RequestPalestra requestPalestra) throws EntityNotFoundException{
        Optional<Palestra> palestra = palestraRepo.findByNomeAndIndirizzo(requestPalestra.getNomePalestra(), requestPalestra.getIndirizzo());
        // controllo se la palestra che si vuole creare è gia presente o meno
        if(palestra.isPresent()) throw new IllegalStateException("Palestra già presente");
        if(requestPalestra.getCap().length() != 5) throw new IllegalStateException("il cap deve essere di 5 numeri");
        Pattern pattern = Pattern.compile(Utils.REGEX_TELEFONO);
        Matcher matcher = pattern.matcher(requestPalestra.getTelefono());
        if(matcher.matches() == false) throw new IllegalStateException("il numero di telefono inserito non rispetta il pattern dei numeri italiani");

        Palestra newPalestra = new Palestra(
                requestPalestra.getCap(),
                requestPalestra.getCitta(),
                requestPalestra.getTelefono(),
                requestPalestra.getIndirizzo(),
                requestPalestra.getNomePalestra(),
                requestPalestra.getEmailPalestra()
        );
        palestraRepo.save(newPalestra);
        return newPalestra;
    }

    public List<ResponsePalestra> getAllPalestre(){
        Iterable<Palestra> palestre = palestraRepo.findAll();
        List<ResponsePalestra> palestreResponse = new ArrayList<>();

        for(Palestra p: palestre){
            palestreResponse.add(ResponsePalestra.builder()
                            .nome(p.getNome())
                            .id(p.getId().toString())
                            .build());
        }
        return palestreResponse;
    }

    public String disiscriviUtente(String email)throws EntityNotFoundException{
        Utente utenteDaDisiscrivere = utenteRepo.findUserByEmail(email).orElseThrow(() -> new EntityNotFoundException("l'email inserita non appartiene a nessun utente"));
        Palestra palestra = utenteDaDisiscrivere.getIscrittiPalestra();
        utenteDaDisiscrivere.setIscrittiPalestra(null);
        List<Utente> utentiIscritti = palestra.getIscrittiPalestra();
        utentiIscritti.remove(utentiIscritti);

        utenteRepo.save(utenteDaDisiscrivere);
        palestraRepo.save(palestra);

        return "utente " + email + " disiscritto correttamente";

    }

    public String iscriviUtentePalestra(HttpServletRequest httpServletRequest, RequestIscrivitiPalestra requestIscrivitiPalestraPalestra){
        Utente utenteLoggato = Utils.getUserFromHeader(httpServletRequest, utenteRepo, jwtUtils);
        Palestra palestaDaIscrivere = palestraRepo.findByNomeAndEmailPalestra(requestIscrivitiPalestraPalestra.getNomePalestra(), requestIscrivitiPalestraPalestra.getEmailPalestra())
                .orElseThrow(() ->new EntityNotFoundException("la palestra cercata non esiste"));
        if(palestaDaIscrivere.getIscrittiPalestra().isEmpty()){
            List<Utente> iscritti = new ArrayList<>();
            iscritti.add(utenteLoggato);
            palestaDaIscrivere.setIscrittiPalestra(iscritti);
            utenteLoggato.setIscrittiPalestra(palestaDaIscrivere);
        }else {
            palestaDaIscrivere.getIscrittiPalestra().add(utenteLoggato);
        }
        utenteRepo.save(utenteLoggato);
        palestraRepo.save(palestaDaIscrivere);

        return "utente "+ utenteLoggato.getEmail() +" iscirtto correttamente alla palestra " + palestaDaIscrivere.getNome();
    }

    public List<Utente> getAllIscrittiBYEmailPalestra(String emailPalestra){
        Palestra palestraCercata = palestraRepo.findByEmailPalestra(emailPalestra) .orElseThrow(() ->new EntityNotFoundException("la palestra cercata non esiste"));;
        List<Utente> utentiIscritti = new ArrayList<>();
        palestraCercata.getIscrittiPalestra().forEach(utente -> utentiIscritti.add(utente));
        return utentiIscritti;
    }
}
