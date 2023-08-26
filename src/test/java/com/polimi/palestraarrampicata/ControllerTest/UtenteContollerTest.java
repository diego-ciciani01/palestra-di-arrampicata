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

    @Test
    @WithMockUser
    public  void  givenUtente_getAllInvitiAccettati_badRequest() throws  Exception{
        given(utenteService.getListInvitiLezioneAccettate(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAll/inviti/accettati"))
                .andExpect(status().isBadRequest());

    }
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
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commento.getId())));
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
     * Questo metodo testa la chiamata al endpoint getListCommentifromUtenteToIstruttore di un utente
     * il metodo vuole ritornare una lista di commenti fatti dall'utente loggato verso un istruttore
     *
     *
     * @throws Exception
     */
    @Test
    @WithMockUser()
    public void getListCommentiFromUtenteToIstruttore_ReturnOk() throws Exception{
        Utente utente = Stub.getUtenteStub();
        Utente istruttore = Stub.getInstructorStub();
        //List<Commento> listaCommenti = Stub.getListCommenti();
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

}

