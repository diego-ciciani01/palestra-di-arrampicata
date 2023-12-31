package com.polimi.palestraarrampicata;
import com.polimi.palestraarrampicata.dto.request.*;
import com.polimi.palestraarrampicata.dto.response.*;
import com.polimi.palestraarrampicata.model.*;
import com.polimi.palestraarrampicata.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Stub {
        //Stub è una classe per utilizzare i dati mokati, usati per effettuare i test
    public static Escursione getEscursioneStub(){
        Escursione escursione = new Escursione();
        escursione.setData(Utils.formatterDataTime("12/03/2023 12:30"));
        escursione.setNomeEscursione("salita del monte rosa");
        escursione.setStatoEscursione(true);
        escursione.setId(1);
        escursione.setOrganizzatore(getInstructorStub());
        escursione.setDescrizione("il giorno 12/03/2023 ci troviamo sotto la funivia del massicio del rosa ....");
        escursione.setUtentiPartecipanti(getListUtentiStub());
        escursione.setPostiDisponibili(33);
        return escursione;
    }

    public static RequestLogin getLoginStub(){
        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setPassword("password");
        requestLogin.setEmail("prova@gmail.com");

        return requestLogin;
    }

    public static RequestRegistrazione getRequestRegistrazione(){
        RequestRegistrazione requestRegistrazione = new RequestRegistrazione();
        requestRegistrazione.setNome("luca");
        requestRegistrazione.setCognome("compagnucci");
        requestRegistrazione.setPassword("password");
        requestRegistrazione.setEmail("luca@gmail.com");
        requestRegistrazione.setDataNascita("28/06/2001");
        requestRegistrazione.setRuolo("UTENTE");

        return requestRegistrazione;

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

    public static Corso getCorsoStub(){
        Corso corso = new Corso();
        corso.setDataInizio(Utils.formatterData("12/03/2023"));
        corso.setNome("arrampicata per principianti");
        corso.setSettimaneDiCorso(5);
        corso.setDifficolta(Difficolta.FACILE);
        corso.setId(1);
        corso.setCosto(60.34f);
        corso.setIstruttoreCorso(getInstructorStub());

        return  corso;
    }

    public static List<Corso> getListCorsoStub(){
        List<Corso> corso = new ArrayList<>();
        for(int i = 0; i<4; i++){
            corso.add(
                    new Corso().builder()
                            .settimaneDiCorso(i)
                            .id(i)
                            .istruttoreCorso(getInstructorStub())
                            .dataInizio(Utils.formatterData("12/01/2023"))
                            .costo(60f)
                            .nome("corso" + i)
                            .build());
        }
        return corso;
    }

    public static Commento getCommentoPadreStub(){
        Commento commento = new Commento();
        commento.setIstruttoreCommentato(getInstructorStub());
        commento.setTesto("Ciao mi sono trovato molto bene a fare la lezione con te");
        commento.setId(1);
        //commento.setDataInserimento(Utils.formatterDataTime("15/05/2023 16:00"));
        return commento;
    }

    public static List<Commento> getListCommenti(){
        List<Commento> commentiList = new ArrayList<>();
        for(int i=0; i<3; i++){
            Commento commento = new Commento();
            if(i!=0){
                commento.setCommentoPadre(commentiList.get(i-1));
            }
            commento.setTesto("commento n " + i);
            commento.setDataInserimento(Utils.formatterDataTime("15/05/2023 12:00"));
            commento.setIstruttoreCommentato(getInstructorStub());
            commento.setId(i);
            commento.setCommentatore(getUtenteStub());
            commentiList.add(commento);
        }
        return commentiList;

    }
    public static List<ResponseEscursione> getResponseEscursione(){
        List<ResponseEscursione> responseEscursione = new ArrayList<>();
        for(int i=0;i<4;i++){
            responseEscursione.add(
                    ResponseEscursione.builder()
                            .id(i)
                            .postiDisponibili(4)
                            .descrizione("portatevi :....")
                            .data(Utils.formatterDataTime("15/05/2023 12:30"))
                            .emailOrganizzatore(getInstructorStub().getEmail())
                            .nomeEscursione("escursione"+i)
                            .build());
        }
        return responseEscursione;

    }


    public static Commento getCommentiFiglioStub(){
        Commento commento = new Commento();
        commento.setId(1);
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
        istruttore.setEmail("marco.grelloni@gmail.com");
        istruttore.setCognome("Grelloni");
        return istruttore;
    }
    public static  Palestra getPalestraStub(){
        Palestra palestra1 = new Palestra();
        palestra1.setId(4);
        palestra1.setNome("manga");
        palestra1.setEmailPalestra("manga.climbing@gmail.com");
        palestra1.setCap("00066");
        palestra1.setCitta("Manziana");
        palestra1.setIndirizzo("Via Delle Fornaci");
        palestra1.setTelefono("3463283458");
        return palestra1;
    }

    public static Utente getUtenteStub(){
        Utente utente = new Utente();
        utente.setId(5);
        utente.setNome("Diego");
        utente.setCognome("Ciciani");
        utente.setEmail("diego.ciciani@gmail.com");
        utente.setRuolo(Ruolo.UTENTE);
        utente.setPassword("password");
        utente.setDataDiNascita(Utils.formatterData("28/06/2001"));
        return utente;

    }
    public static String getJwtStub_User() {
        return "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTIzNDU2Nzg5LCJuYW1lIjoiSm9zZXBoIn0.OpOSSw7e485LOP5PrzScxHb7SR6sAOMRckfFwi4rp7o";
    }
    public static List<Utente> getListUtentiStub(){
        List<Utente> utentiList = new ArrayList<>();
        for(int i=0; i<1; i++){
            Utente utente = new Utente();
            utente.setId(i);
            utente.setNome("_user_" + i);
            utente.setFotoProfilo(null);
            utente.setRuolo(Ruolo.UTENTE);
            utente.setEmail(i + "useremail@email.com");
            utente.setPassword("password"+i);;
            utente.setDataDiNascita(Utils.formatterData("28/06/2001"));
            utentiList.add(utente);
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
            taglie.add(taglia);
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
    public static List<Noleggio> getListNoleggioStub(){
        ArrayList<Noleggio> noleggiList = new ArrayList<>();
        for(Integer i=0;i<4;i++){
            noleggiList.add(
                    Noleggio.builder()
                            .id(i)
                            .dataNoleggio(Utils.formatterDataTime("15/03/2024 12:20"))
                            .dataFineNoleggio(Utils.formatterDataTime("16/03/2024 12:20"))
                            .build());
        }
        return noleggiList;
    }

    public static Attrezzatura getAttrezzoStub(){
        Attrezzatura attrezzo = new Attrezzatura();
        attrezzo.setId(1);
        attrezzo.setAttrezziPalestra(getPalestraStub());
        attrezzo.setDisponibilita(true);
        attrezzo.setNomeAttrezzatura("imbrago");

        return attrezzo;

    }

    public static ResponseCorso getResponseCorsoStub(){
        ResponseCorso responseCorso = new ResponseCorso();
        responseCorso.setId("2");
        responseCorso.setNome("corso base");
        responseCorso.setEmailIstruttore("istruttore1@gmail.com");
        return  responseCorso;
    }

    public static List<ResponseCorso> getListResponseCorsoStub(){
        List<ResponseCorso> responseCorso = new ArrayList<>();
        for(Integer i=0; i<3; i++){
            responseCorso.add(
                    ResponseCorso.builder()
                            .nome("corso"+i)
                            .numeroSettimane(i)
                            .id(i.toString())
                            .emailIstruttore(getInstructorStub().getEmail())
                            .build());
        }
        return responseCorso;
    }

    public static List<Attrezzatura> getListAttrezziStub(){
        List<Attrezzatura> attrezzi = new ArrayList<>();
        for(int i = 1; i<5; i++){
            Attrezzatura attrezzo = new Attrezzatura();
            attrezzo.setId(i);
            attrezzo.setDisponibilita(true);
            attrezzo.setNomeAttrezzatura("corda_"+i);
            attrezzo.setAttrezziPalestra(getPalestraStub());
            attrezzi.add(attrezzo);
        }
        return  attrezzi;
    }
    public static List<ResponseAttrezzatura> getListResponseAttrezzatura(){
        List<ResponseAttrezzatura> getResponseAttrezzaturaList = new ArrayList<>();
        for(Integer i = 0; i<4; i++){
            getResponseAttrezzaturaList.add(
                    ResponseAttrezzatura.builder()
                            .id(i.toString())
                            .nomeAttrezzo("imbrago")
                            .disponibilita(true)
                            .nomePalestraAppartenente(getPalestraStub().getNome())
                            .build());
        }

        return getResponseAttrezzaturaList;
    }

     public  static RequestAttrezzatura getRequestAttrezzaturaStub(){
        RequestAttrezzatura requestAttrezzatura = new RequestAttrezzatura();
        requestAttrezzatura.setIdPalestraPossessore(getPalestraStub().getId().toString());
        requestAttrezzatura.setNomeAttrezzo("imbrago");
        requestAttrezzatura.setDisponibilita("true");
        requestAttrezzatura.setQuantita("10");

        return requestAttrezzatura;

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
                            .utenteMittente(getListUtentiStub())
                            .istruttore(getInstructorStub())
                            .build()

            );

        }
        return lezioniInviti;
    }
    public static List<Palestra> getListPalestraStub(){
        List<Palestra> palestraList = new ArrayList<>();
        for (int i =0; i<4; i++){
            palestraList.add(
                    Palestra.builder()
                            .id(i)
                            .cap("0000"+i)
                            .emailPalestra("palestra"+i+"@gmail.com")
                            .citta("citta" +1)
                            .indirizzo("via roma n " + i)
                            .nome("arrampichiamocitutti"+i)
                            .build()
            );
        }
        return palestraList;
    }

    public static RequestIscriviti getRequestIscrizione(){
        RequestIscriviti requestIscriviti = new RequestIscriviti();
        requestIscriviti.setId(1);
        return  requestIscriviti;
    }

    public static List <ResponseAttrezzatura> getResponseAttrezzaturaList(){
        List <ResponseAttrezzatura> responseAttrezzaturaList = new ArrayList<>();
        for(Integer i=0; i<3;i++){
            responseAttrezzaturaList.add(
                    ResponseAttrezzatura.builder()
                            .nomeAttrezzo("attrezzo"+i)
                            .quantitaDisponibile(5)
                            .disponibilita(true)
                            .nomeAttrezzo("imbrago")
                            .id(i.toString())
                            .nomePalestraAppartenente(getPalestraStub().getNome())
                            .build()
            );
        }
        return responseAttrezzaturaList;
    }

    public  static Lezione getLezione(){
        Lezione lezione = new Lezione();
        lezione.setTipologiaLezione(TipologiaLezione.Introduzione);
        lezione.setStatoLezione(true);
        lezione.setData(Utils.formatterDataTime("12/12/2023 12:30"));
        lezione.setDurata(3f);
        lezione.setId(0);
        lezione.setIstruttore(getInstructorStub());

        return lezione;
    }

    public static List<ResponsePalestra> getListResponsePalestraStub(){
        List<ResponsePalestra> responsePalestraList = new ArrayList<>();
        for(Integer i =0; i<4; i++){
            responsePalestraList.add(
                   ResponsePalestra.builder()
                           .nome("arrampichiamocitutti"+i)
                           .id(i.toString())
                           .build());
        }
        return responsePalestraList;
    }

    public static RequestNoleggiaAttrezzatura getRequesNoleggioStub(){
        RequestNoleggiaAttrezzatura requestNoleggiaAttrezzatura = new RequestNoleggiaAttrezzatura();
        requestNoleggiaAttrezzatura.setQuantita("3");
        requestNoleggiaAttrezzatura.setNomeAttrezzo("imbrago");
        requestNoleggiaAttrezzatura.setTaglia("M");
        requestNoleggiaAttrezzatura.setDataInizioNoleggio("14/11/2023");
        requestNoleggiaAttrezzatura.setDataFineNoleggio("17/11/2023");

        return requestNoleggiaAttrezzatura;
    }

    public static List <ResponseNoleggio> getNoleggiaAttrezzatura(){
        List<ResponseNoleggio> responseNoleggiaAttrezzaturaList = new ArrayList<>();
        for(int i=0;i<3;i++){
            responseNoleggiaAttrezzaturaList.add(
                     ResponseNoleggio.builder()
                             .id("1")
                             .dataInizioNoleggio("12/10/2023")
                             .dataFineNoleggio("15/10/2023")
                             .build()
            );
        }
        return responseNoleggiaAttrezzaturaList;
    }

}
