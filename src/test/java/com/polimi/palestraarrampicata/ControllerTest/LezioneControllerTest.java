package com.polimi.palestraarrampicata.ControllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.request.RequestAccettaRiffiuta;
import com.polimi.palestraarrampicata.dto.request.RequestLezione;
import com.polimi.palestraarrampicata.dto.response.ResponseLezione;
import com.polimi.palestraarrampicata.mapping.Request;
import com.polimi.palestraarrampicata.model.Lezione;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.service.LezioneService;
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

import java.time.DateTimeException;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
@SpringBootTest
@RunWith(SpringRunner.class)
public class LezioneControllerTest {

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private LezioneService lezioneService;
    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }
    //Usato per mappare gli oggetti come stringhe, in modo da poterli passare nel conenututo dalla richiesta mockata
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Caso di test per la creazione di una nuova lezione.
     * Il test simula la creazione di una lezione utilizzando un contesto utente fittizio.
     * Viene configurato il comportamento del lezioneService per restituire un oggetto Lezione simulato,
     * quindi viene inviata una richiesta POST all'endpoint appropriato per creare una lezione.
     * Il test si aspetta uno stato di risposta HTTP 200 OK.
     */
    @Test
    @WithMockUser()
    public void creaLezione_ReturOk() throws Exception{
        Lezione lezione = Stub.getLezione();
        RequestLezione requestLezione = Request.toRequestLezioneByLezioneMapper(lezione);
        String requestEscursione_string = objectMapper.writeValueAsString(requestLezione);
        when(lezioneService.createLesson(any(),any())).thenReturn(lezione);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lezione/crea")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestEscursione_string))
                .andExpect(status().isOk());

    }

    /**
     * Caso di test per la gestione di una richiesta di creazione di una nuova lezione che solleva un'eccezione di stato illegale.
     * Il test simula la creazione di una lezione utilizzando un contesto utente fittizio.
     * Viene configurato il comportamento del lezioneService per sollevare un'eccezione di stato illegale quando chiamato.
     * Successivamente, viene inviata una richiesta POST all'endpoint appropriato per creare una lezione.
     * Il test si aspetta uno stato di risposta HTTP 400 Bad Request in risposta all'eccezione sollevata.
     */
    @Test
    @WithMockUser()
    public  void creaLezione_ReturnBadRequest_IllegalState() throws Exception{
        given(lezioneService.createLesson(any(),any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lezione/crea")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    /**
     * Caso di test per la gestione di una richiesta di creazione di una nuova lezione che solleva un'eccezione di entità non trovata.
     * Il test simula la creazione di una lezione utilizzando un contesto utente fittizio.
     * Viene configurato il comportamento del lezioneService per sollevare un'eccezione di entità non trovata quando chiamato.
     * Successivamente, viene inviata una richiesta POST all'endpoint appropriato per creare una lezione.
     * Il test si aspetta uno stato di risposta HTTP 400 Bad Request in risposta all'eccezione sollevata.
     */
    @Test
    @WithMockUser()
    public  void creaLezione_ReturnBadRequest_EntityNotFound() throws Exception{
        Lezione lezione = Stub.getLezione();
        RequestLezione requestLezione = Request.toRequestLezioneByLezioneMapper(lezione);
        given(lezioneService.createLesson(any(),any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lezione/crea")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso di test per la gestione di una richiesta di creazione di una nuova lezione che solleva un'eccezione di data e ora non valide.
     * Il test simula la creazione di una lezione utilizzando un contesto utente fittizio.
     * Viene configurato il comportamento del lezioneService per sollevare un'eccezione di DateTimeException quando chiamato.
     * Successivamente, viene inviata una richiesta POST all'endpoint appropriato per creare una lezione.
     * Il test si aspetta uno stato di risposta HTTP 400 Bad Request in risposta all'eccezione sollevata.
     */

    @Test
    @WithMockUser()
    public  void creaLezione_ReturnBadRequest_DateTimeExeption() throws Exception{
        Lezione lezione = Stub.getLezione();
        RequestLezione requestLezione = Request.toRequestLezioneByLezioneMapper(lezione);
        String requestEscursione_string = objectMapper.writeValueAsString(requestLezione);
        given(lezioneService.createLesson(any(),any())).willThrow(new DateTimeException("data non valida"));
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lezione/crea")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    /**
     * Caso di test per la gestione di una richiesta di recupero di tutte le lezioni esistenti che restituisce una risposta HTTP di successo.
     * Il test simula la richiesta di recupero di tutte le lezioni utilizzando un contesto utente fittizio.
     * Viene configurato il comportamento del lezioneService per restituire una lista di lezioni simulata.
     * Successivamente, viene inviata una richiesta GET all'endpoint appropriato per ottenere la lista delle lezioni.
     * Il test si aspetta uno stato di risposta HTTP 200 OK in risposta alla richiesta.
     */
    @Test
    @WithMockUser()
    public void getAll_Lezioni_ReturnOk() throws Exception{
        List<ResponseLezione> lezioneList =Stub.getLezioneResponseStub();
        String listLezioniResponse_str = objectMapper.writeValueAsString(lezioneList);
        given(lezioneService.getListLession()).willReturn(lezioneList);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lezione/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(listLezioniResponse_str))
                .andExpect(status().isOk());

    }

    /**
     * Caso di test per la gestione di una richiesta di recupero di tutte le lezioni esistenti che solleva un'eccezione di tipo EntityNotFoundException.
     * Il test simula la richiesta di recupero di tutte le lezioni utilizzando un contesto utente fittizio.
     * Viene configurato il comportamento del lezioneService per sollevare un'eccezione EntityNotFoundException.
     * Successivamente, viene inviata una richiesta GET all'endpoint appropriato per ottenere la lista delle lezioni.
     * Il test si aspetta uno stato di risposta HTTP 500 Internal Server Error in risposta alla richiesta.
     */
    @Test
    @WithMockUser()
    public void getAll_Lezioni_ReturnOk_EntityNotFoundException() throws Exception{
        List<ResponseLezione> lezioneList =Stub.getLezioneResponseStub();
        String listLezioniResponse_str = objectMapper.writeValueAsString(lezioneList);
        given(lezioneService.getListLession()).willThrow(new  EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lezione/getAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listLezioniResponse_str))
                .andExpect(status().isInternalServerError());

    }

    /**
     * Caso di test per la gestione dell'accettazione di un invito a una lezione da parte di un istruttore.
     * Il test simula l'accettazione di un invito per una lezione utilizzando un contesto utente fittizio con ruolo "ISTRUTTORE".
     * Viene preparata una lezione simulata e una corrispondente richiesta di accettazione dell'invito.
     * Il mock lezioneService è configurato per restituire la lezione stessa quando viene chiamato il metodo accettaRifiutaLezione().
     * Successivamente, viene inviata una richiesta POST all'endpoint appropriato per accettare l'invito alla lezione.
     * Il test si aspetta uno stato di risposta HTTP 200 OK in risposta alla richiesta.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ISTRUTTORE"})
    public void accettaInvito_requestOk() throws Exception{
        Lezione lezione = Stub.getLezione();
        RequestAccettaRiffiuta requestAccettaRiffiuta = Request.toRequestAcettaRifiutaByLezioneMapper(lezione);
        String lezione_str = objectMapper.writeValueAsString(requestAccettaRiffiuta);
        given(lezioneService.accettaRifiutaLezione(any(),any())).willReturn(lezione);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lezione/invito/accetta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lezione_str))
                .andExpect(status().isOk());
    }
    /**
     * Caso di test per verificare l'accesso negato (HTTP 403 Forbidden) durante l'accettazione di un invito a una lezione.
     * Il test simula l'accettazione di un invito per una lezione utilizzando un contesto utente fittizio senza ruoli specifici.
     * Viene preparata una lezione simulata e una corrispondente richiesta di accettazione dell'invito.
     * Il mock lezioneService è configurato per restituire la lezione stessa quando viene chiamato il metodo accettaRifiutaLezione().
     * Successivamente, viene inviata una richiesta POST all'endpoint appropriato per accettare l'invito alla lezione.
     * Il test si aspetta uno stato di risposta HTTP 403 Forbidden in risposta alla richiesta.
     */

    @Test
    @WithMockUser()
    public void accettaInvito_requestForbidden() throws Exception{
        Lezione lezione = Stub.getLezione();
        RequestAccettaRiffiuta requestAccettaRiffiuta = Request.toRequestAcettaRifiutaByLezioneMapper(lezione);
        String lezione_str = objectMapper.writeValueAsString(requestAccettaRiffiuta);
        given(lezioneService.accettaRifiutaLezione(any(),any())).willReturn(lezione);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lezione/invito/accetta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(lezione_str))
                .andExpect(status().isForbidden());
    }

    /**
     * Caso di test per verificare una richiesta malformata (HTTP 400 Bad Request) durante l'accettazione di un invito a una lezione,
     * a causa di una EntityNotFoundException generata dal servizio.
     * Il test simula l'accettazione di un invito per una lezione utilizzando un contesto utente istruttore.
     * Il mock lezioneService è configurato per generare una EntityNotFoundException quando viene chiamato il metodo accettaRifiutaLezione().
     * Successivamente, viene inviata una richiesta POST all'endpoint appropriato per accettare l'invito alla lezione.
     * Il test si aspetta uno stato di risposta HTTP 400 Bad Request in risposta alla richiesta.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ISTRUTTORE"})
    public void accettaInvito_BadRequest_EntityNotFound() throws Exception{
        given(lezioneService.accettaRifiutaLezione(any(),any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lezione/invito/accetta")
                        .contentType(MediaType.APPLICATION_JSON))
                     .andExpect(status().isBadRequest());
    }

    /**
     * Caso di test per verificare una richiesta malformata (HTTP 400 Bad Request) durante l'accettazione di un invito a una lezione,
     * a causa di un IllegalStateException generato dal servizio.
     * Il test simula l'accettazione di un invito per una lezione utilizzando un contesto utente istruttore.
     * Il mock lezioneService è configurato per generare un IllegalStateException quando viene chiamato il metodo accettaRifiutaLezione().
     * Successivamente, viene inviata una richiesta POST all'endpoint appropriato per accettare l'invito alla lezione.
     * Il test si aspetta uno stato di risposta HTTP 400 Bad Request in risposta alla richiesta.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ISTRUTTORE"})
    public void accettaInvito_BadRequest_IllegalState() throws Exception{
        given(lezioneService.accettaRifiutaLezione(any(),any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/lezione/invito/accetta")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso di test per verificare una risposta corretta (HTTP 200 OK) quando si richiedono le lezioni di un istruttore.
     * Il test simula una richiesta di tutte le lezioni di un istruttore utilizzando un contesto utente.
     * Il mock lezioneService è configurato per restituire una lista di lezioni quando viene chiamato il metodo getLessionByInstructor().
     * Successivamente, viene inviata una richiesta GET all'endpoint appropriato per ottenere le lezioni dell'istruttore.
     * Il test si aspetta uno stato di risposta HTTP 200 OK in risposta alla richiesta.
     */
    @Test
    @WithMockUser()
    public void getLesson_ByInstructor_ReturnOk() throws Exception {
        List<ResponseLezione> responseLezioneList = Stub.getLezioneResponseStub();
        Utente istruttore = Stub.getInstructorStub();
        given(lezioneService.getLessionByInstructor(any())).willReturn(responseLezioneList);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lezione/getAll/byInstructor/"+istruttore.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(istruttore.getId().toString()))
                .andExpect(status().isOk());
    }

    /**
     * Caso di test per verificare una risposta di errore interno del server (HTTP 500 Internal Server Error)
     * quando si verifica un'eccezione di tipo EntityNotFoundException durante la richiesta delle lezioni di un istruttore.
     * Il test simula una richiesta di tutte le lezioni di un istruttore utilizzando un contesto utente.
     * Il mock lezioneService è configurato per lanciare un'eccezione EntityNotFoundException quando viene chiamato il metodo getLessionByInstructor().
     * Successivamente, viene inviata una richiesta GET all'endpoint appropriato per ottenere le lezioni dell'istruttore.
     * Il test si aspetta uno stato di risposta HTTP 500 Internal Server Error in risposta alla richiesta.
     */
    @Test
    @WithMockUser()
    public void getLesson_ByInstructor_ReturnInternalServerError_EntityNotFound() throws Exception {
        Utente istruttore = Stub.getInstructorStub();
        given(lezioneService.getLessionByInstructor(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/lezione/getAll/byInstructor/"+istruttore.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(istruttore.getId().toString()))
                .andExpect(status().isInternalServerError());
    }
}
