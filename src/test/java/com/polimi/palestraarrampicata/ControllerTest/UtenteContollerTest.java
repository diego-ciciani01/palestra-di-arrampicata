package com.polimi.palestraarrampicata.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.request.RequestValutazione;
import com.polimi.palestraarrampicata.dto.response.ResponseCommento;
import com.polimi.palestraarrampicata.dto.response.ResponseLezione;
import com.polimi.palestraarrampicata.dto.response.ResponseValutazione;
import com.polimi.palestraarrampicata.mapping.Request;
import com.polimi.palestraarrampicata.mapping.Response;
import com.polimi.palestraarrampicata.model.Commento;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.model.Valutazione;
import com.polimi.palestraarrampicata.service.UtenteService;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UtenteContollerTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UtenteService utenteService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * Caso di test per verificare il recupero di tutte le lezioni accettate da parte di un utente autenticato (HTTP 200 OK).
     * Il test simula una richiesta GET all'endpoint /api/v1/user/getAll/inviti/accettati.
     * Il mock utenteService è configurato per restituire una lista di lezioni accettate come risultato della chiamata.
     * Il test si aspetta uno stato di risposta HTTP 200 OK e controlla che il corpo della risposta contenga i dati delle lezioni attesi.
     */
    @Test
    @WithMockUser
    public void givenUtente_getAllLessonsAccettate_ReturnOk() throws Exception{
        List<ResponseLezione> responseLezioni = Stub.getLezioniResponseStubAccettate();
        given(utenteService.getListInvitiLezioneAccettate(any())).willReturn(responseLezioni);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAll/inviti/accettati"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].istruttore", is("istruttore1@email.it")))
                .andExpect(jsonPath("$[0].statoLezione", is(true)))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].istruttore", is("istruttore2@email.it")))
                .andExpect(jsonPath("$[1].statoLezione", is(true)))
                .andExpect(jsonPath("$[2].id", is("3")))
                .andExpect(jsonPath("$[2].istruttore", is("istruttore3@email.it")))
                .andExpect(jsonPath("$[2].statoLezione", is(true)));
    }

    /**
     * Caso di test per verificare la gestione di una richiesta di recupero di tutti gli inviti lezione accettati
     * da parte di un utente autenticato con uno stato di errore Bad Request (HTTP 400 Bad Request).
     * Il test simula una richiesta GET all'endpoint /api/v1/user/getAll/inviti/accettati.
     * Il mock utenteService è configurato per lanciare un'eccezione EntityNotFoundException.
     * Il test si aspetta uno stato di risposta HTTP 400 Bad Request.
     */
    @Test
    @WithMockUser
    public  void  givenUtente_getAllInvitiAccettati_badRequest() throws  Exception{
        given(utenteService.getListInvitiLezioneAccettate(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAll/inviti/accettati"))
                .andExpect(status().isBadRequest());

    }

    /**
     * Caso di test per verificare il recupero di tutti gli inviti lezione di un utente autenticato con stato di successo (HTTP 200 OK).
     * Il test simula una richiesta GET all'endpoint /api/v1/user/getAllInviti.
     * Il mock utenteService è configurato per restituire una lista di inviti lezione simulati.
     * Il test verifica che la risposta HTTP abbia uno stato 200 OK e controlla i dettagli degli inviti nella risposta JSON.
     */
    @Test
    @WithMockUser
    public void givenUtente_getAllInviti_returnOk() throws Exception {
        List<ResponseLezione> Responselezioni = Stub.getLezioneResponseStub();
       given(utenteService.getListInvitiLezione(any())).willReturn(Responselezioni);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAllInviti"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].istruttore", is("istruttore1@email.it")))
                .andExpect(jsonPath("$[0].statoLezione", is(false)))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].istruttore", is("istruttore2@email.it")))
                .andExpect(jsonPath("$[1].statoLezione", is(false)))
                .andExpect(jsonPath("$[2].id", is("3")))
                .andExpect(jsonPath("$[2].istruttore", is("istruttore3@email.it")))
                .andExpect(jsonPath("$[2].statoLezione", is(false)));

    }
    /**
     * Caso di test per verificare il recupero di tutti gli inviti lezione di un utente autenticato con stato di fallimento (HTTP 400 Bad Request).
     * Il test simula una richiesta GET all'endpoint /api/v1/user/getAllInviti.
     * Il mock utenteService è configurato per sollevare un'eccezione di tipo EntityNotFoundException.
     * Il test verifica che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser
    public  void  givenUtente_getAllInviti_badRequest() throws  Exception{
        given(utenteService.getListInvitiLezione(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAllInviti"))
                .andExpect(status().isBadRequest());

    }

    /**
     *  Questo metodo testa la chiamata al endpoint commentaIstruttore, utilizzando JUnit e Mockito.
     *  viene preso un oggetto commento e un oggetto response commento dallo stub,
     *  vengono settati poi dei parametri
     *  Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     *  "/api/v1/user/commenta/istruttore" passando la request di commento.
     *  Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 Ok) e il valore dell'oggetto di ritorno
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void creaCommento_returnOk()throws Exception{
        Commento commento = Stub.getCommentiFiglioStub();
        Commento commentoPadre = Stub.getCommentoPadreStub();
        Utente utente = Stub.getUtenteStub();
        RequestCommento requestCommento = Request.toRequestCommentoByCommentoMapper(commento);
        requestCommento.setIdCommentoPadre(commentoPadre.getId().toString());

        String requestCommento_asString = new ObjectMapper().writeValueAsString(requestCommento);
        given(utenteService.creaCommento(any(), any())).willReturn(commento);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/commenta/istruttore")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(requestCommento_asString))
                        .andExpect(jsonPath("$.id", is(commento.getId().toString())))
                        .andExpect(status().isOk());
    }

    /**
     * Questo metodo testa la chiamata al'endpoint commentaIstruttore, utilizzando JUnit e Mockito.
     * Viene mockato una L'id del commento padre con un dato sbagliato
     * Viene passato alla chiamata  la request con gli errori, affinche la chiamata sollevi IllegalStateException().
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/commento/istruttrore/" passando la request sbagliata.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (500 Internal Server Error)
     * @throws Exception
     */

    @Test
    @WithMockUser
    public void creaCommento_returnBadRequest_IllegalState()throws Exception{
        Commento commentoFiglio = Stub.getCommentiFiglioStub();
        Commento commentoPadre = Stub.getCommentiFiglioStub();
        commentoFiglio.setId(2);
        commentoFiglio.setCommentoPadre(commentoPadre);
        commentoPadre.setId(1);
        RequestCommento requestCommento = Request.toRequestCommentoByCommentoMapper(commentoFiglio);
        requestCommento.setIdCommentoPadre("10");
        given(utenteService.creaCommento(any(), any())).willThrow(new IllegalStateException());
        String requestCommentoString = new ObjectMapper().writeValueAsString(requestCommento);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/commenta/istruttore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestCommentoString))
                .andExpect(status().isBadRequest());

    }
    /**
     * Questo metodo testa la chiamata al'endpoint commentaIstruttore, utilizzando JUnit e Mockito.
     * Viene mockato una L'id del commento padre con un dato sbagliato
     * Viene passato alla chiamata  la request con gli errori, affinche la chiamata sollevi EntityNotFoundException().
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/commento/istruttrore/" passando la request sbagliata.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (500 Internal Server Error)
     * @throws Exception
     */

    @Test
    @WithMockUser
    public void creaCommento_returnBadRequest_EntityNotFound()throws Exception{
        Commento commentoFiglio = Stub.getCommentiFiglioStub();
        Commento commentoPadre = Stub.getCommentiFiglioStub();
        commentoFiglio.setId(2);
        commentoFiglio.setCommentoPadre(commentoPadre);
        commentoPadre.setId(1);
        RequestCommento requestCommento = Request.toRequestCommentoByCommentoMapper(commentoFiglio);
        requestCommento.setIdCommentoPadre("10");
        given(utenteService.creaCommento(any(), any())).willThrow(new EntityNotFoundException());
        String requestCommentoString = new ObjectMapper().writeValueAsString(requestCommento);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/commenta/istruttore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestCommentoString))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(value = "spring", authorities = {"UTENTE"})
    public  void creaValutazione_returnOk()throws Exception{
        Valutazione valutazione =  Stub.getValutazioneStub();
        RequestValutazione requestValutazione = Request.toRequestValutazioneByValutazioneMapper(valutazione);
        ResponseValutazione responseValutazione = Response.toValutazioneResponseByValutazioneMapper(valutazione);
        String requestValutazione_asString = new ObjectMapper().writeValueAsString(requestValutazione);
        given(utenteService.creaValutazione(any(), any())).willReturn(valutazione);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/valuta/istruttore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestValutazione_asString))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(Integer.parseInt(responseValutazione.getId()))))
                        .andExpect(jsonPath("$.valore", is(Integer.parseInt(responseValutazione.getValore()))));


    }

    /**
     * Questo metodo testa la chiamata al endpoint CreaValutazione di un utente, utilizzando JUnit e Mockito.
     * Viene mockato una valutazione con dei valori sbagliatti.
     * Viene passato alla chiamata  la request con gli errori, affinche la chiamata sollevi IllegalStateException().
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/valuta/istruttrore/" passando la request sbagliata.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (500 Internal Server Error)
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"UTENTE"})
    public  void creaValutazione_returnBadRequest()throws Exception{
        Valutazione valutazione = Stub.getValutazioneStub();
        RequestValutazione requestValutazione = Request.toRequestValutazioneByValutazioneMapper(valutazione);
        requestValutazione.setEmailValutato(null);
        String requestValutazione_asString = new ObjectMapper().writeValueAsString(requestValutazione);
        given(utenteService.creaValutazione( any(), any())).willThrow(new IllegalStateException() );
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/valuta/istruttore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestValutazione_asString))
                .andExpect(status().isInternalServerError());

    }

    /**
     * Questo metodo testa la chiamata al endpoint creaValutazione di un utente
     * Il metodo può essere invocato solo da UTENTE e per testare che questo sia vero non setto il @WithMockUser, così
     * considera la chiamata come se fosse fatta da un Istruttore.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/delete/email/" passando il valore della mail corretto che è quello mockato in utente
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (403 Forbidden).
     * @throws Exception
     */
    @Test
    @WithMockUser()
    public  void creaValutazione_returnForbidden()throws Exception{
        Valutazione valutazione = Stub.getValutazioneStub();
        RequestValutazione requestValutazione = Request.toRequestValutazioneByValutazioneMapper(valutazione);
        requestValutazione.setEmailValutato(null);
        String requestValutazione_asString = new ObjectMapper().writeValueAsString(requestValutazione);
        given(utenteService.creaValutazione( any(), any())).willThrow(new IllegalStateException() );
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/valuta/istruttore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestValutazione_asString))
                        .andExpect(status().isForbidden());

    }


    /**
     * Questo metodo testa la chiamata al endpoint deleteUtente di un utente, utilizzando JUnit e Mockito.
     * Viene mockato un utente con dei valori corretti.
     * Viene poi chiamato il metodo deleteUtente() presente nel service di Utente, e ci aspettiamo che il metodo
     * ritorni una stringa.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/delete/email/" passando il valore corretto della mail di un utente
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (200 Ok) e il valore della stringa di ritorno
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenString_DeleteUtente_returnOK() throws  Exception{
        Utente utente = Stub.getUtenteStub();
        String response = "L'utente " + utente.getEmail() + " è stato eliminato correttamente";
        given(utenteService.deleteUserByEmail(any())).willReturn(response);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/delete/email/" + utente.getEmail()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(response));

    }

    /**
     * Questo metodo testa la chiamata al endpoint deleteUtente di un utente che avviene sbagliando i parametri
     * Viene mockato uno user con una email non valida
     * Viene passato alla chiamata il valore della email non valido affinchè il metodo sollevi un IllegalStateException().
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/delete/email/" passando il valore della email falsi.
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (500 internalServerError).
     * @throws Exception
     */

    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenString_DeleteUtente_returnIternal_Server_Error() throws Exception{
        Utente utente = Stub.getUtenteStub();
        utente.setEmail("email errata ");
        given(utenteService.deleteUserByEmail(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/delete/email/" + utente.getEmail()))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Questo metodo testa la chiamata al endpoint deleteUtente di un utente
     * Il metodo può essere invocato solo da ADMIN e per testare che questo sia vero non setto il @WithMockUser, così
     * considera la chiamata come se fosse fatta da un utente generico.
     * Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     * "/api/v1/user/delete/email/" passando il valore della mail corretto che è quello mockato in utente
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (403 Forbidden).
     * @throws Exception
     */
    @Test
    @WithMockUser()
    public void givenString_DeleteUtente_Return_Forbidden() throws Exception{
        Utente utente = Stub.getUtenteStub();
        utente.setEmail("email errata ");
        given(utenteService.deleteUserByEmail(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/delete/email/" + utente.getEmail()))
                .andExpect(status().isForbidden());
    }
    /**
     * Caso di test per verificare il recupero di tutti i commenti da un utente a un istruttore autenticato con successo (HTTP 200 OK).
     * Il test simula una richiesta GET all'endpoint /api/v1/user/commenti/getAllByIstruttore/{istruttoreId}.
     * Il mock utenteService è configurato per restituire una lista di commenti di risposta specificata.
     * Il test verifica che la risposta HTTP abbia uno stato 200 OK e controlla alcuni attributi chiave dei commenti restituiti.
     */
    @Test
    @WithMockUser()
    public void getListCommentiFromUtenteToIstruttore_ReturnOk() throws Exception{
        Utente istruttore = Stub.getInstructorStub();
        List<ResponseCommento> responseCommenti = Response.toCommentoListResponseByCommentoListMapper(Stub.getListCommenti());
        given(utenteService.getListCommentifromUtenteToIstruttore(any(), any())).willReturn(responseCommenti);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/commenti/getAllByIstruttore/" + istruttore.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("0")))
                .andExpect(jsonPath("$[1].id", is("1")))
                .andExpect(jsonPath("$[2].id", is("2")))
                .andExpect(jsonPath("$[0].emailIstruttore", is(istruttore.getEmail())))
                .andExpect(jsonPath("$[1].emailIstruttore", is(istruttore.getEmail())))
                .andExpect(jsonPath("$[2].emailIstruttore", is(istruttore.getEmail())));

    }

    /**
     * Caso di test per verificare la gestione di una richiesta errata (HTTP 400 Bad Request) quando si tenta di ottenere
     * la lista di commenti da un utente a un istruttore e l'istruttore non viene trovato.
     * Il test simula una richiesta GET all'endpoint /api/v1/user/commenti/getAllByIstruttore/{istruttoreId}
     * con un istruttore non valido.
     * Il mock utenteService è configurato per lanciare un'eccezione EntityNotFoundException.
     * Il test verifica che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser()
    public void getListCommentiFromUtenteToIstruttore_BadRequest() throws Exception{
        Utente istruttore = null;
        String responseCommentiString = new ObjectMapper().writeValueAsString(istruttore);
        given(utenteService.getListCommentifromUtenteToIstruttore(any(), any())).willThrow(new EntityNotFoundException());
        String msg = "istruttore non trovato";
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/commenti/getAllByIstruttore/" + 1)
                .content(msg))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test per verificare il comportamento del controller quando viene richiesta la media delle valutazioni di un istruttore.
     * Deve restituire una risposta OK con il messaggio contenente la media delle valutazioni.
     * Configura il comportamento del service per restituire il messaggio di media delle valutazioni
     * Esegue la richiesta mock per ottenere la media delle valutazioni dell'istruttore
     * @throws Exception Se si verificano eccezioni durante l'esecuzione del test.
     *
     */
    @Test
    @WithMockUser()
    public  void getAvg_FromEmailIstruttore_ReturOk() throws Exception{
        Utente istruttore = Stub.getInstructorStub();
        String msg = "la media dell'istruttore è di 4.3";

        given(utenteService.getAvgValutazione(any())).willReturn(msg);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/valuta/getAvg/" + istruttore.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(istruttore.getEmail()))
                .andExpect(status().isOk());

    }

    /**
     * Test per verificare il comportamento del controller quando si richiede la media delle valutazioni di un istruttore,
     * ma si verifica uno stato di errore a livello di stato interno (IllegalStateException).
     * Deve restituire uno stato di errore HTTP BadRequest.
     * @throws Exception Se si verificano eccezioni durante l'esecuzione del test.
     */
    @Test
    @WithMockUser()
    public  void getAvg_FromEmailIstruttore_ReturnBadRequest_IllegalState() throws Exception{
        Utente istruttore = Stub.getInstructorStub();
        given(utenteService.getAvgValutazione(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/valuta/getAvg/" + istruttore.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(istruttore.getEmail()))
                .andExpect(status().isBadRequest());

    }

    /**
     * Test per verificare il comportamento del controller quando si richiede la media delle valutazioni di un istruttore,
     * ma si verifica uno stato di errore a livello di entità non trovata (EntityNotFoundException).
     * Deve restituire uno stato di errore HTTP BadRequest.
     * @throws Exception Se si verificano eccezioni durante l'esecuzione del test.
     */
    @Test
    @WithMockUser()
    public  void getAvg_FromEmailIstruttore_ReturnBadRequest_EntityNotFound() throws Exception{
        Utente istruttore = Stub.getInstructorStub();
        given(utenteService.getAvgValutazione(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/valuta/getAvg/" + istruttore.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(istruttore.getEmail()))
                .andExpect(status().isBadRequest());

    }
}

