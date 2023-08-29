package com.polimi.palestraarrampicata.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.request.RequestCorso;
import com.polimi.palestraarrampicata.dto.request.RequestIscriviti;
import com.polimi.palestraarrampicata.dto.response.ResponseCorso;
import com.polimi.palestraarrampicata.mapping.Request;
import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.service.CorsoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CorsoControllerTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CorsoService corsoService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }
    //Usato per mappare gli oggetti come stringhe, in modo da poterli passare nel conenututo dalla richiesta mockata
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Testa il comportamento dell'endpoint per creare un corso quando l'utente ha il ruolo ADMIN.
     * Verifica che una richiesta POST a /api/v1/corso/crea restituisca uno stato di successo (HTTP 200 OK).
     * Il mock corsoService è configurato per restituire un oggetto Corso durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void givenCorso_CreaCorso_ReturnOk() throws Exception{
        Corso corso = Stub.getCorsoStub();
        RequestCorso requestCorso = Request.toRequestCorsoByCorsoMapper(corso);
        String requestCorso_string = new ObjectMapper().writeValueAsString(requestCorso);
        Mockito.when(corsoService.creaCorso(any())).thenReturn(corso);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/corso/crea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestCorso_string))
                        .andExpect(status().isOk());

    }
    /**
     * Testa il comportamento dell'endpoint per creare un corso quando l'utente ha il ruolo ADMIN.
     * Verifica che una richiesta POST a /api/v1/corso/crea restituisca uno stato di errore bad request (HTTP 400).
     * Il mock corsoService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta,
     * simboleggiando una situazione in cui l'entità dell'istruttore associato al corso non è trovata.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})

    public void givenCorso_CreaCorso_ReturnBadRequest() throws Exception{
        Corso corso = Stub.getCorsoStub();
        RequestCorso requestCorso = Request.toRequestCorsoByCorsoMapper(corso);
        requestCorso.setEmailIstruttore("email sbagliata");
        String requestCorso_string = new ObjectMapper().writeValueAsString(requestCorso);
        Mockito.when(corsoService.creaCorso(any())).thenThrow(new EntityNotFoundException());
            assertTrue(true);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/corso/crea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestCorso_string))
                .andExpect(status().isBadRequest());

    }

    /**
     * Testa il comportamento dell'endpoint per eliminare un corso quando l'utente ha il ruolo ADMIN.
     * Verifica che una richiesta DELETE a /api/v1/corso/elimina/{id} restituisca uno stato OK (HTTP 200).
     * Il mock corsoService è configurato per restituire un oggetto ResponseCorso quando viene chiamato il metodo eliminaCorso.
     * Viene eseguita la chiamata alla richiesta DELETE e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void deleteCorso_ReturnOK() throws Exception{
        ResponseCorso responseCorso = Stub.getResponseCorsoStub();
        given(corsoService.eliminaCorso(any())).willReturn(responseCorso);
        String responseCorsoString = new ObjectMapper().writeValueAsString(responseCorso);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/corso/elimina/" + "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(responseCorsoString))
                .andExpect(status().isOk());
    }
    /**
     * Testa il comportamento dell'endpoint per eliminare un corso quando l'utente ha il ruolo ADMIN,
     * ma si verifica un'eccezione EntityNotFoundException all'interno del corsoService.
     * Verifica che una richiesta DELETE a /api/v1/corso/elimina/{id} restituisca uno stato di errore interno del server (HTTP 500 Internal Server Error).
     * Il mock corsoService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta DELETE e viene verificato che la risposta HTTP abbia uno stato 500 Internal Server Error.
     */

    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void deleteCorso_ReturnInternalServerError_EntityNotFound() throws Exception{
        ResponseCorso responseCorso = Stub.getResponseCorsoStub();
        given(corsoService.eliminaCorso(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/corso/elimina/" +"2")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isInternalServerError());
    }

    /**
     * Questo metodo testa la chiamata al endpoint deleteCorso di un Corso
     * Il metodo può essere invocato solo da ADMIN e per testare che questo sia vero non setto il @WithMockUser, così
     * considera la chiamata come se fosse fatta da un utente generico.
     * Viene successivamente effettuata un chiamata Http di tipo delete all'indirizzo
     * "/api/v1/corso/elimina/+id"   passando un valore di id fittizio
     * Infine, viene controllato l'oggetto di risposta e confrontato con lo status (403 Forbidden).
     * @throws Exception
     */
    @Test
    @WithMockUser()
    public void deleteCorso_ReturnForbidden() throws Exception{
        given(corsoService.eliminaCorso(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/corso/elimina/"+ 2)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutti i corsi di un istruttore quando l'utente ha effettuato l'accesso.
     * Verifica che una richiesta GET a /api/v1/corso/getAll/byInstructor/{id_istruttore} restituisca uno stato di successo (HTTP 200 OK).
     * Il mock corsoService è configurato per restituire una lista di corsi come risposta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser()
    public void getAll_Corso_ByIstructor_ReturnOk() throws Exception{
        List<ResponseCorso> responseCorsi = Stub.getListResponseCorsoStub();
        Utente istruttore = Stub.getInstructorStub();
        String responseCorsiString = new ObjectMapper().writeValueAsString(responseCorsi);
        given(corsoService.getLessionByInstructor(any())).willReturn(responseCorsi);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/corso/getAll/byInstructor/" + istruttore.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(responseCorsiString))
                .andExpect(status().isOk());


    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutti i corsi di un istruttore quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta GET a /api/v1/corso/getAll/byInstructor/{id_istruttore} restituisca uno stato di errore interno del server (HTTP 500 Internal Server Error).
     * Il mock corsoService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 500 Internal Server Error.
     */
    @Test
    @WithMockUser()
    public void getAll_Corso_ByIstructor_ReturnInternalServerError_EntityNotFound ()throws Exception{
        Utente istruttore = Stub.getInstructorStub();
        given(corsoService.getLessionByInstructor(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/corso/getAll/byInstructor/" + istruttore.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

    }

    /**
     * Testa il comportamento dell'endpoint per iscriversi a un corso con un utente autorizzato.
     * Verifica che una richiesta POST a /api/v1/corso/iscriviti restituisca uno stato OK (HTTP 200 OK)
     * e che il corpo della risposta contenga l'ID del corso a cui l'utente si è iscritto.
     * Il mock corsoService è configurato per restituire un corso durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 200 OK
     * e che il corpo della risposta contenga l'ID del corso restituito dal mock.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"UTENTE"})
    public void iscriviCorso_ReturnOk() throws Exception{
        RequestIscriviti requestIscriviti = Stub.getRequestIscrizione();
        Corso corso = Stub.getCorsoStub();
        String requestIscriviti_string = new ObjectMapper().writeValueAsString(requestIscriviti);
        given(corsoService.iscrivitiCorso(any(), any())).willReturn(corso);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/corso/iscriviti")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestIscriviti_string))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(corso.getId())));


    }

    /**
     * Testa il comportamento dell'endpoint per l'iscrizione a un corso quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta POST a /api/v1/corso/iscriviti restituisca uno stato di errore interno del server (HTTP 500 Internal Server Error).
     * Il mock corsoService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta di iscrizione.
     * Viene preparata una richiesta di iscrizione di esempio e viene configurato il mock per gestire l'eccezione.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 500 Internal Server Error.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"UTENTE"})
    public void iscriviCorso_ReturnInternaServerError_EntityNotFound() throws Exception{
        RequestIscriviti requestIscriviti = Stub.getRequestIscrizione();
        String requestIscriviti_string = new ObjectMapper().writeValueAsString(requestIscriviti);
        given(corsoService.iscrivitiCorso(any(), any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/corso/iscriviti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestIscriviti_string))
                        .andExpect(status().isInternalServerError());

    }

    /**
     * Testa il comportamento dell'endpoint per l'iscrizione a un corso quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta POST a /api/v1/corso/iscriviti restituisca uno stato di errore Forbidden (HTTP 403 Forbidden).
     * Il mock corsoService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta di iscrizione.
     * Viene preparata una richiesta di iscrizione di esempio e viene configurato il mock per gestire l'eccezione.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 403 Forbidden.
     */
    @Test
    @WithMockUser()
    public void iscriviCorso_ReturnForbidden() throws Exception{
        RequestIscriviti requestIscriviti = Stub.getRequestIscrizione();
        String requestIscriviti_string = new ObjectMapper().writeValueAsString(requestIscriviti);
        given(corsoService.iscrivitiCorso(any(), any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/corso/iscriviti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestIscriviti_string))
                .andExpect(status().isForbidden());

    }



}
