package com.polimi.palestraarrampicata;

import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.request.RequestIscrivitiPalestra;
import com.polimi.palestraarrampicata.dto.response.ResponseLezione;
import com.polimi.palestraarrampicata.model.*;
import com.polimi.palestraarrampicata.service.LezioneService;
import com.polimi.palestraarrampicata.utils.Utils;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.List;

public class Stub {

    public static Escursione getEscursioneStub(){
        Escursione escursione = new Escursione();
        escursione.setData(Utils.formatterDataTime("12/03/2023 12:30"));
        escursione.setNomeEscursione("salita del monte rosa");
        escursione.setStatoEscursione(true);
        escursione.setId(1);
        escursione.setOrganizzatore(getInstructorStub());
        escursione.setDescrizione("il giorno 12/03/2023 ci troviamo sotto la funivia del massicio del rosa ....");
        escursione.setUtentiPartecipanti(getListUtentiStub());
        return escursione;
    }

    public static List<ResponseLezione> getLezioneResponseStub(){
        List<ResponseLezione> inivitiUtente = new ArrayList<>();
        inivitiUtente.add(ResponseLezione.builder().dataLezione("12/03/2023").statoLezione(false).istruttore("istruttore1@email.it").id("1").build());
        inivitiUtente.add(ResponseLezione.builder().dataLezione("13/03/2023").statoLezione(false).istruttore("istruttore2@email.it").id("2").build());
        inivitiUtente.add(ResponseLezione.builder().dataLezione("14/03/2023").statoLezione(false).istruttore("istruttore3@email.it").id("3").build());

        return  inivitiUtente;
    }

    public static List<ResponseLezione> getLezioniResponseStubAccettate(){
        List<ResponseLezione> inivitiUtenteAccettati = new ArrayList<>();
        inivitiUtenteAccettati.add(ResponseLezione.builder().dataLezione("12/03/2023").statoLezione(true).istruttore("istruttore1@email.it").id("1").build());
        inivitiUtenteAccettati.add(ResponseLezione.builder().dataLezione("13/03/2023").statoLezione(true).istruttore("istruttore2@email.it").id("2").build());
        inivitiUtenteAccettati.add(ResponseLezione.builder().dataLezione("14/03/2023").statoLezione(true).istruttore("istruttore3@email.it").id("3").build());

        return  inivitiUtenteAccettati;
    }

    public static Corso getCorspStub(){
        Corso corso = new Corso();
        corso.setDataInizio(Utils.formatterData("12/03/2023"));
        corso.setNome("arrampicata per principianti");
        corso.setSettimaneDiCorso(5);
        corso.setDifficolta(Difficolta.FACILE);
        corso.setId(1);
        corso.setCosto(60.34f);
        corso.setIscritti(getListUtentiStub());

        return  corso;
    }

    public static Commento getCommentoPadreStub(){
        Commento commento = new Commento();
        commento.setIstruttoreCommentato(getInstructorStub());
        commento.setTesto("Ciao mi sono trovato molto bene a fare la lezione con te");
        commento.setId(1);
        commento.setDataInserimento(Utils.formatterDataTime("15/05/2023 16:00"));
        return commento;
    }

    public static Commento getCommentiFiglioStub(){
        Commento commento = new Commento();
        commento.setTesto("Grazie mille per il feedback, se vuoi puoi lasciare una valutazione");
        commento.setDataInserimento(Utils.formatterDataTime("15/05/2023 12:00"));
        commento.setIstruttoreCommentato(getInstructorStub());
        return commento;
    }

    public static RequestCommento getRequestCommento(){
        RequestCommento requestCommento = new RequestCommento();
        requestCommento.setTesto("Grazie mille per il feedback, se vuoi puoi lasciare una valutazione");
        requestCommento.setIdCommentoPadre("2");

        return requestCommento;
    }

    public static Valutazione getValutazioneStub(){
        Valutazione valutazione = new Valutazione();
        valutazione.setValore(3);
        valutazione.setId(1);
        valutazione.setValutato(getInstructorStub());
        valutazione.setValutatore(getUtenteStub());

        return valutazione;
    }

    public static Utente getInstructorStub(){
        Utente istruttore = new Utente();
        istruttore.setId(1);
        istruttore.setNome("Marco");
        istruttore.setRuolo(Ruolo.ISTRUTTORE);
        istruttore.setCognome("Grelloni");
        return istruttore;
    }
    public static  Palestra getPalestraStub(){
        Palestra palestra1 = new Palestra();
        palestra1.setId(1);
        palestra1.setIscrittiPalestra(getListUtentiStub());
        palestra1.setNome("Monkei");
        palestra1.setEmailPalestra("monkei.climbing@gmail.com");
        palestra1.setCap("00066");
        palestra1.setCitta("Manziana");
        palestra1.setIndirizzo("Via Delle Fornaci");
        palestra1.setTelefono("3422283456");
        return palestra1;
    }

    public static  Utente getUtenteStub(){
        Utente utente = new Utente();
        utente.setId(5);
        utente.setNome("Diego");
        utente.setCognome("Ciciani");
        utente.setEmail("diego.ciciani@gmail.com");
        utente.setRuolo(Ruolo.UTENTE);
        utente.setPassword("password");
        return  utente;

    }
    public static String getJwtStub_User() {
        return "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTIzNDU2Nzg5LCJuYW1lIjoiSm9zZXBoIn0.OpOSSw7e485LOP5PrzScxHb7SR6sAOMRckfFwi4rp7o";
    }
    public static List<Utente> getListUtentiStub(){
        List<Utente> utentiList = new ArrayList<>();
        for(int i=1; i<4; i++){
            Utente utente = new Utente();
            utente.setId(i);
            utente.setNome("_user_" + i);
            utente.setCognome("_cognome_" + i);
            utente.setRuolo(Ruolo.UTENTE);
            utente.setEmail(i + "useremail@email.com");
            utente.setPassword("password"+i);
            utente.setEscursioniPartecipate((List<Escursione>) getEscursioneStub());
            utente.setInviti(getAllInvitiLezioneStub());
            utente.setIscrittiPalestra(getPalestraStub());
        }
    return utentiList;
    }

    public static List<Taglia> getListTaglieStub(){
        List<Taglia> taglie = new ArrayList<>();
        for(int i = 1; i<5; i++){
            Taglia taglia = new Taglia();
            taglia.setId(i);
            taglia.setQuantita(10);
            taglia.setTagliaAttrezzo(Integer.toString(i));
            taglia.setAttrezzo(getAttrezzoStub());
        }
        return taglie;
    }

    public static Noleggio getNoleggioStub(){
        Noleggio noleggio = new Noleggio();
        noleggio.setId(1);
        noleggio.setDataNoleggio(Utils.formatterDataTime("15/03/2024 12:20"));
        noleggio.setDataFineNoleggio(Utils.formatterDataTime("16/03/2024 12:20"));
        noleggio.setAttrezzoNoleggiato(getAttrezzoStub());
        noleggio.setNoleggiatore(getInstructorStub());
        return noleggio;
    }

    public static Attrezzatura getAttrezzoStub(){
        Attrezzatura attrezzo = new Attrezzatura();
        attrezzo.setId(6);
        attrezzo.setAttrezziPalestra(getPalestraStub());
        attrezzo.setDisponibilita(true);
        attrezzo.setNomeTaglia(getListTaglieStub());
        attrezzo.setNomeAttrezzatura("imbrago");

        return attrezzo;

    }

    public static List<Attrezzatura> getListAttrezziStub(){
        List<Attrezzatura> attrezzi = new ArrayList<>();
        for(int i = 1; i<5; i++){
            Attrezzatura attrezzo = new Attrezzatura();
            attrezzo.setId(i);
            attrezzo.setDisponibilita(true);
            attrezzo.setNomeTaglia(getListTaglieStub());
            attrezzo.setNomeAttrezzatura("corda_"+i);
            attrezzo.setAttrezziPalestra(getPalestraStub());
        }
        return  attrezzi;
    }

    public static List<Lezione> getAllInvitiLezioneStub(){
        List<Lezione> lezioniInviti = new ArrayList<>();
        for(int i =1; i<10; i++){
            lezioniInviti.add(
                    Lezione.builder()
                            .id(i)
                            .statoLezione(false)
                            .istruttore(getInstructorStub())
                            .tipologiaLezione(TipologiaLezione.Introduzione)
                            .durata(2.30F)
                            .utentiInvitati(getListUtentiStub())
                            .istruttore(getInstructorStub())
                            .build()
            );
        }
        return lezioniInviti;
    }
}
