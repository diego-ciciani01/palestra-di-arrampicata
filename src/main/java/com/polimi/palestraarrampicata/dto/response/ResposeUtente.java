package com.polimi.palestraarrampicata.dto.response;

import com.polimi.palestraarrampicata.model.Commento;
import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.Utente;
import lombok.Data;

@Data
public class ResposeUtente {
    private Integer id;
    private String nome;
    private String username;
    private  byte[] fotoProfilo;
    private Ruolo ruolo;
    private  String email;

    public ResposeUtente(Utente utente){
        this.id = utente.getId();
        this.nome = utente.getNome();
        this.username = utente.getUsername();
        this.fotoProfilo = utente.getFotoProfilo();
        this.email = utente.getEmail();
        this.ruolo = utente.getRuolo();
    }

    public  ResposeUtente(Integer id, String username){
        this.id = id;
        this.username = username;
    }

}
