package com.polimi.palestraarrampicata.service;


import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtenteService implements UserDetailsService {
    @Autowired
    private UtenteRepo utenteRepo;

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

}
