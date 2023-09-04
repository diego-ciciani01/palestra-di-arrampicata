package com.polimi.palestraarrampicata.observer;

import com.polimi.palestraarrampicata.model.Utente;
import lombok.Data;

@Data
public class ObserverUser implements Observer{
    //utente
    private Utente utente;

    public ObserverUser(Utente utente){
        this.utente = utente;
    }

    @Override
    public void update() {
        System.out.println("Il corso è stato aggiornato" + utente.getEmail());

    }
}
