package com.polimi.palestraarrampicata.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.request.RequestLogin;
import com.polimi.palestraarrampicata.dto.request.RequestModificaUtente;
import com.polimi.palestraarrampicata.dto.request.RequestRegistrazione;
import com.polimi.palestraarrampicata.exception.LoginFallito;
import com.polimi.palestraarrampicata.exception.RegistrazioneFallita;
import com.polimi.palestraarrampicata.mapping.Request;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.service.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProfiloControllerTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private ProfileService profileService;
    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }
    //Usato per mappare gli oggetti come stringhe, in modo da poterli passare nel conenututo dalla richiesta mockata
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Testa il comportamento dell'endpoint per l'autenticazione di un utente.
     * Verifica che una richiesta POST a /api/v1/profilo/login restituisca uno stato OK (HTTP 200 OK)
     * quando l'autenticazione ha successo.
     * Il mock profileService è configurato per restituire un token JWT di esempio.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato OK.
     */
    @Test
    @WithMockUser
    public void login_returnOk() throws Exception{
        RequestLogin requestLogin = Stub.getLoginStub();
        String jwt = Stub.getJwtStub_User();
        String requestLogin_str = objectMapper.writeValueAsString(requestLogin);
        given(profileService.login(any())).willReturn(jwt);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestLogin_str))
                .andExpect(status().isOk());

    }

    /**
     * Testa il comportamento dell'endpoint per l'autenticazione di un utente.
     * Verifica che una richiesta POST a /api/v1/profilo/login restituisca uno stato Unauthorized (HTTP 401 Unauthorized)
     * quando l'autenticazione fallisce.
     * Il mock profileService è configurato per lanciare un'eccezione LoginFallito.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato Unauthorized.
     */
    @Test
    @WithMockUser
    public void login_returnUnathorized() throws Exception{
        RequestLogin requestLogin = Stub.getLoginStub();
        String requestLogin_str = objectMapper.writeValueAsString(requestLogin);
        given(profileService.login(any())).willThrow(new LoginFallito());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestLogin_str))
                .andExpect(status().isUnauthorized());

    }
    /**
     * Testa il comportamento dell'endpoint per l'autenticazione di un utente.
     * Verifica che una richiesta POST a /api/v1/profilo/login restituisca uno stato Internal Server Error (HTTP 500 Internal Server Error)
     * quando si verifica un'eccezione EntityNotFoundException.
     * Il mock profileService è configurato per lanciare un'eccezione EntityNotFoundException.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato Internal Server Error.
     */
    @Test
    @WithMockUser
    public void login_returnInternalServerError_InternalServerError() throws Exception{
        RequestLogin requestLogin = Stub.getLoginStub();
        String requestLogin_str = objectMapper.writeValueAsString(requestLogin);
        given(profileService.login(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestLogin_str))
                        .andExpect(status().isInternalServerError());

    }

    /**
     * Testa il comportamento dell'endpoint per la registrazione di un nuovo utente.
     * Verifica che una richiesta POST a /api/v1/profilo/registrazione restituisca uno stato OK (HTTP 200 OK)
     * quando la registrazione viene completata con successo.
     * Il mock profileService è configurato per restituire un utente fittizio.
     * Viene creata una richiesta di registrazione di esempio e il mock profileService restituisce l'utente fittizio.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato OK.
     */
    @Test
    @WithMockUser
    public void registrazione_returnOk() throws  Exception{
        RequestRegistrazione requestRegistrazione = Stub.getRequestRegistrazione();
        Utente utente = Stub.getUtenteStub();
        String requestRegistrazione_str = objectMapper.writeValueAsString(requestRegistrazione);
        given(profileService.registrazione(any())).willReturn(utente);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/registrazione")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestRegistrazione_str))
                .andExpect(status().isOk());
    }

    /**
     * Testa il comportamento dell'endpoint per la registrazione di un nuovo utente.
     * Verifica che una richiesta POST a /api/v1/profilo/registrazione restituisca uno stato BadRequest (HTTP 400 Bad Request)
     * quando la registrazione fallisce a causa di parametri mancanti.
     * Il mock profileService è configurato per lanciare un'eccezione RegistrazioneFallita con un messaggio specifico.
     * Viene creata una richiesta di registrazione di esempio e il mock profileService lancia l'eccezione.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato BadRequest.
     */

    @Test
    @WithMockUser
    public void registrazione_returnBadRequest() throws  Exception{
        RequestRegistrazione requestRegistrazione = Stub.getRequestRegistrazione();
        Utente utente = Stub.getUtenteStub();
        utente.setEmail(null);
        String requestRegistrazione_str = objectMapper.writeValueAsString(requestRegistrazione);
        given(profileService.registrazione(any())).willThrow( new RegistrazioneFallita("parametri mancanti"));
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/registrazione")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestRegistrazione_str))
                .andExpect(status().isBadRequest());
    }

    /**
     * Testa il comportamento dell'endpoint per la registrazione di un nuovo utente.
     * Verifica che una richiesta POST a /api/v1/profilo/registrazione restituisca uno stato BadRequest (HTTP 400 Bad Request)
     * quando la richiesta di registrazione non include tutti i parametri richiesti.
     * Il mock profileService è configurato per restituire un utente fittizio.
     * Viene creata una richiesta di registrazione senza alcuni parametri e il mock profileService restituisce un utente fittizio.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato BadRequest.
     */
    @Test
    @WithMockUser
    public void registrazione_returnBadRequest_parametrimancanti() throws  Exception{
        RequestRegistrazione requestRegistrazione = Stub.getRequestRegistrazione();
        Utente utente = Stub.getUtenteStub();
        given(profileService.registrazione(any())).willReturn(utente);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/registrazione")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    /**
     * Testa il comportamento dell'endpoint per la registrazione di un nuovo utente.
     * Verifica che una richiesta POST a /api/v1/profilo/registrazione restituisca uno stato InternalServerError (HTTP 500 Internal Server Error)
     * quando si verifica un errore interno nel servizio di registrazione. Il mock profileService è configurato per lanciare un'eccezione di tipo EntityNotFoundException.
     * Viene creata una richiesta di registrazione e il mock profileService lancia un'eccezione simulata.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato InternalServerError.
     */
    @Test
    @WithMockUser
    public void registrazione_returnInternalServerError() throws  Exception{
        RequestRegistrazione requestRegistrazione = Stub.getRequestRegistrazione();
        Utente utente = Stub.getUtenteStub();
        utente.setEmail(null);
        String requestRegistrazione_str = objectMapper.writeValueAsString(requestRegistrazione);
        given(profileService.registrazione(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/registrazione")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestRegistrazione_str))
                        .andExpect(status().isInternalServerError());
    }

    /**
     * Testa il comportamento dell'endpoint per la modifica di un utente.
     * Verifica che una richiesta POST a /api/v1/profilo/modifica restituisca uno stato OK (HTTP 200 OK)
     * quando la modifica dell'utente è eseguita con successo.
     * Il mock profileService è configurato per restituire un messaggio di conferma simulato.
     * Viene creato un oggetto Utente e una richiesta di modifica corrispondente.
     * Il mock profileService restituisce un messaggio simulato quando la modifica viene eseguita.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato OK.
     */
    @Test
    @WithMockUser
    public void modificaUtene_returnOk()throws Exception{
        Utente utente = Stub.getUtenteStub();
        RequestModificaUtente requestModificaUtente = Request.toRequestModificaUtenteByUtenteMapper(utente);
        String msg = "utente modificato correttamente";
        String requestModificaUtente_str = objectMapper.writeValueAsString(requestModificaUtente);

        given(profileService.modificaUtente(any(), any())).willReturn(msg);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/modifica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestModificaUtente_str))
                .andExpect(status().isOk());
    }

    /**
     * Testa il comportamento dell'endpoint per la modifica di un utente.
     * Verifica che una richiesta POST a /api/v1/profilo/modifica restituisca uno stato Internal Server Error (HTTP 500 Internal Server Error)
     * quando si verifica un'eccezione di tipo EntityNotFoundException durante la modifica dell'utente.
     * Il mock profileService è configurato per lanciare un'eccezione EntityNotFoundException simulata.
     * Viene creato un oggetto Utente e una richiesta di modifica corrispondente.
     * Il mock profileService lancia un'eccezione simulata quando la modifica viene eseguita.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato Internal Server Error.
     */
    @Test
    @WithMockUser
    public void modificaUtente_returnInternalServerError_EntityNotFound() throws Exception{
        Utente utente = Stub.getUtenteStub();
        RequestModificaUtente requestModificaUtente = Request.toRequestModificaUtenteByUtenteMapper(utente);
        String msg = "utente modificato correttamente";
        String requestModificaUtente_str = objectMapper.writeValueAsString(requestModificaUtente);
        given(profileService.modificaUtente(any(), any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/modifica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestModificaUtente_str))
                .andExpect(status().isInternalServerError());
    }
    /**
     * Testa il comportamento dell'endpoint per la modifica di un utente.
     * Verifica che una richiesta POST a /api/v1/profilo/modifica restituisca uno stato Bad Request (HTTP 400 Bad Request).
     * Il mock profileService è configurato per restituire un messaggio di successo durante la modifica dell'utente.
     * Viene creato un oggetto Utente e una richiesta di modifica corrispondente.
     * Il mock profileService restituisce un messaggio simulato di successo.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato Bad Request.
     */
    @Test
    @WithMockUser
    public void modificaUtente_returnBadRequest() throws Exception{
        Utente utente = Stub.getUtenteStub();
        RequestModificaUtente requestModificaUtente = Request.toRequestModificaUtenteByUtenteMapper(utente);
        String msg = "utente modificato correttamente";
        String requestModificaUtente_str = objectMapper.writeValueAsString(requestModificaUtente);
        given(profileService.modificaUtente(any(),any())).willReturn(msg);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/profilo/modifica")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

}
