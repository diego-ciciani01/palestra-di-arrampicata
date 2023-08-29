package com.polimi.palestraarrampicata.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.request.RequestEscursione;
import com.polimi.palestraarrampicata.dto.response.ResponseEscursione;
import com.polimi.palestraarrampicata.mapping.Request;
import com.polimi.palestraarrampicata.model.Escursione;
import com.polimi.palestraarrampicata.service.EscursioneService;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@SpringBootTest
@RunWith(SpringRunner.class)
public class EscursioneControllerTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private EscursioneService escursioneService;
    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    private ObjectMapper  objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Caso di test per verificare la creazione di un'escursione con successo (HTTP 200 OK).
     * Il test simula una richiesta POST all'endpoint /api/v1/escursione/crea per creare un'escursione.
     * Il mock escursioneService è configurato per restituire l'oggetto escursione.
     * Il test verifica che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ISTRUTTORE"})
    public void create_Escursione_ReturnOk() throws Exception{
        Escursione escursione  = Stub.getEscursioneStub();
        RequestEscursione requestEscursione = Request.toRequestEscursioneByEscursioneMapper(escursione);
        String requestEscursione_string = new ObjectMapper().writeValueAsString(requestEscursione);
        given(escursioneService.createEscursione(any(),any())).willReturn(escursione);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/escursione/crea")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestEscursione_string))
                .andExpect(status().isOk());

    }
    /**
     * Caso di test per verificare che la creazione di un'escursione restituisca uno stato di forbidden (HTTP 403 Forbidden).
     * Il test simula una richiesta POST all'endpoint /api/v1/escursione/crea per creare un'escursione.
     * Il mock escursioneService è configurato per lanciare un'eccezione di tipo IllegalStateException.
     * Il test verifica che la risposta HTTP abbia uno stato 403 Forbidden.
     */
    @Test
    @WithMockUser()
    public void create_Escursione_ReturnForbidden() throws Exception{
        Escursione escursione  = Stub.getEscursioneStub();
        RequestEscursione requestEscursione = Request.toRequestEscursioneByEscursioneMapper(escursione);
        String requestEscursione_string = new ObjectMapper().writeValueAsString(requestEscursione);
        given(escursioneService.createEscursione(any(),any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/escursione/crea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestEscursione_string))
                        .andExpect(status().isForbidden());
    }
    /**
     * Caso di test per verificare che la creazione di un'escursione restituisca uno stato di bad request (HTTP 400 Bad Request).
     * Il test simula una richiesta POST all'endpoint /api/v1/escursione/crea per creare un'escursione.
     * Il mock escursioneService è configurato per lanciare un'eccezione di tipo EntityNotFoundException.
     * Il test verifica che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ISTRUTTORE"})
    public void create_Escursione_ReturnForbidden_EntityNotFound() throws Exception{
        Escursione escursione  = Stub.getEscursioneStub();
        RequestEscursione requestEscursione = Request.toRequestEscursioneByEscursioneMapper(escursione);
        String requestEscursione_string = new ObjectMapper().writeValueAsString(requestEscursione);
        given(escursioneService.createEscursione(any(),any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/escursione/crea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestEscursione_string))
                .andExpect(status().isBadRequest());
    }
    /**
     * Caso di test per verificare che la creazione di un'escursione restituisca uno stato di bad request (HTTP 400 Bad Request).
     * Il test simula una richiesta POST all'endpoint /api/v1/escursione/crea per creare un'escursione.
     * Il mock escursioneService è configurato per lanciare un'eccezione di tipo DateTimeException con un messaggio di errore personalizzato.
     * Il test verifica che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ISTRUTTORE"})
    public void create_Escursione_ReturnForbidden_Data() throws Exception{
        Escursione escursione  = Stub.getEscursioneStub();
        RequestEscursione requestEscursione = Request.toRequestEscursioneByEscursioneMapper(escursione);
        String requestEscursione_string = new ObjectMapper().writeValueAsString(requestEscursione);
        given(escursioneService.createEscursione(any(),any())).willThrow(new DateTimeException("formato data inseito errato"));
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/escursione/crea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestEscursione_string))
                .andExpect(status().isBadRequest());
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutte le escursioni disponibili.
     * Verifica che una richiesta GET a /api/v1/escursione/getAll restituisca uno stato di successo (HTTP 200 OK).
     * Il mock escursioneService è configurato per restituire una lista di escursioni di prova.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser()
    public void getAll_EscursioniDisponibili_ReturnOk()throws Exception{
        List<ResponseEscursione> responseEscursione = Stub.getResponseEscursione();
        given(escursioneService.getListEscursioniDisponibili()).willReturn(responseEscursione);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/escursione/getAll")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
    /**
     * Testa il comportamento dell'endpoint per ottenere tutte le escursioni disponibili quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta GET a /api/v1/escursione/getAll restituisca uno stato di errore interno del server (HTTP 500 Internal Server Error).
     * Il mock escursioneService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 500 Internal Server Error.
     */
    @Test
    @WithMockUser()
    public void getAll_EscursioniDisponibili_ReturnInternalServer_EntityNotFound()throws Exception{
        List<ResponseEscursione> responseEscursione = Stub.getResponseEscursione();
        String responseEscursione_string = objectMapper.writeValueAsString(responseEscursione);
        given(escursioneService.getListEscursioniDisponibili()).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/escursione/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

    }

    /**
     * Testa il comportamento dell'endpoint per iscriversi a un'escursione restituendo uno stato HTTP OK (200).
     * Verifica che una richiesta POST a /api/v1/escursione/iscriviti/{id} restituisca uno stato HTTP OK.
     * Il mock escursioneService è configurato per restituire un'escursione quando viene chiamato il metodo partecipaEscursione.
     * Viene eseguita la chiamata alla richiesta POST con l'ID dell'escursione e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"UTENTE"})
    public void iscriviti_escursione_RerurnOk()throws Exception{
        Escursione escursione = Stub.getEscursioneStub();
        Integer id = 1;
        String id_str = objectMapper.writeValueAsString(id);
        given(escursioneService.partecipaEscursione(any(), any())).willReturn(escursione);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/escursione/iscriviti/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(id_str))
                .andExpect(status().isOk());
    }

    /**
     * Testa il comportamento dell'endpoint per iscriversi a un'escursione quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta POST a /api/v1/escursione/iscriviti/{id} restituisca uno stato di errore interno del server (HTTP 500 Internal Server Error).
     * Il mock escursioneService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST con l'ID dell'escursione e viene verificato che la risposta HTTP abbia uno stato 500 Internal Server Error.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"UTENTE"})
    public void iscriviti_escursione_RerurnBadRequest_EntityNotFound()throws Exception{
        Escursione escursione = Stub.getEscursioneStub();
        Integer id = 1;
        String id_str = objectMapper.writeValueAsString(id);
        given(escursioneService.partecipaEscursione(any(), any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/escursione/iscriviti/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(id_str))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Testa il comportamento dell'endpoint per iscriversi a un'escursione quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta POST a /api/v1/escursione/iscriviti/{id} restituisca uno stato di errore Forbidden (HTTP 403 Forbidden).
     * Il mock escursioneService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST con l'ID dell'escursione e viene verificato che la risposta HTTP abbia uno stato 403 Forbidden.
     */
    @Test
    @WithMockUser()
    public void iscriviti_escursione_RerurnForbidden()throws Exception{
        Escursione escursione = Stub.getEscursioneStub();
        Integer id = 1;
        String id_str = objectMapper.writeValueAsString(id);
        given(escursioneService.partecipaEscursione(any(), any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/escursione/iscriviti/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(id_str))
                .andExpect(status().isForbidden());
    }

    /**
     * Testa il comportamento dell'endpoint per eliminare un'escursione.
     * Verifica che una richiesta DELETE a /api/v1/escursione/elimina/{id} restituisca uno stato OK (HTTP 200 OK).
     * Il mock escursioneService è configurato per restituire una risposta con i dettagli dell'escursione eliminata.
     * Viene eseguita la chiamata alla richiesta DELETE con l'ID dell'escursione e viene verificato che la risposta HTTP abbia uno stato 200 OK e contenga i dettagli dell'escursione eliminata.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ISTRUTTORE"})
    public void eliminaEscursione_ReturnOk() throws Exception{
        ResponseEscursione responseEscursione = Stub.getResponseEscursione().get(0);
        Integer id = 0;
        String id_str = objectMapper.writeValueAsString(id);
        given(escursioneService.eliminaEscursione(any())).willReturn(responseEscursione);
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/escursione/elimina/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(id_str))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(responseEscursione.getId())));

    }
    /**
     * Testa il comportamento dell'endpoint per eliminare un'escursione quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta DELETE a /api/v1/escursione/elimina/{id} restituisca uno stato di errore interno del server (HTTP 500 Internal Server Error).
     * Il mock escursioneService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta DELETE con l'ID dell'escursione e viene verificato che la risposta HTTP abbia uno stato 500 Internal Server Error.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ISTRUTTORE"})
    public void eliminaEscursione_ReturInternaServerError() throws Exception{
        ResponseEscursione responseEscursione = Stub.getResponseEscursione().get(0);
        Integer id = 10;
        String id_str = objectMapper.writeValueAsString(id);
        given(escursioneService.eliminaEscursione(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/escursione/elimina/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(id_str))
                .andExpect(status().isInternalServerError());

    }
    /**
     * Testa il comportamento dell'endpoint per eliminare un'escursione quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta DELETE a /api/v1/escursione/elimina/{id} restituisca uno stato di errore Forbidden (HTTP 403 Forbidden).
     * Il mock escursioneService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta DELETE con l'ID dell'escursione e viene verificato che la risposta HTTP abbia uno stato 403 Forbidden.
     */
    @Test
    @WithMockUser()
    public void eliminaEscursione_ReturForbidden() throws Exception{
        Integer id = 10;
        String id_str = objectMapper.writeValueAsString(id);
        given(escursioneService.eliminaEscursione(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/escursione/elimina/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(id_str))
                .andExpect(status().isForbidden());

    }
    /**
     * Testa il comportamento dell'endpoint per ottenere tutte le escursioni create da un istruttore quando la richiesta è valida.
     * Verifica che una richiesta GET a /api/v1/escursione/getAll/byIstruttore/{idIstruttore} restituisca uno stato OK (HTTP 200 OK).
     * Il mock escursioneService è configurato per restituire una lista di escursioni.
     * Viene eseguita la chiamata alla richiesta GET con l'ID dell'istruttore e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser()
    public void getEscursione_ByIstruttore_ReturnOk() throws Exception{
        List<ResponseEscursione> getListEscursione = Stub.getResponseEscursione();
        Integer id = Stub.getInstructorStub().getId();
        String id_str = objectMapper.writeValueAsString(id);
        given(escursioneService.getListEscursioniByIstruttore(any())).willReturn(getListEscursione);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/escursione/getAll/byIstruttore/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(id_str))
                .andExpect(status().isOk());
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutte le escursioni create da un istruttore quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta GET a /api/v1/escursione/getAll/byIstruttore/{idIstruttore} restituisca uno stato di errore interno del server (HTTP 500 Internal Server Error).
     * Il mock escursioneService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET con l'ID dell'istruttore e viene verificato che la risposta HTTP abbia uno stato 500 Internal Server Error.
     */
    @Test
    @WithMockUser()
    public void getEscursione_ByIstruttore_ReturInternalServerError_EntityNotFound() throws Exception{

        Integer id = Stub.getInstructorStub().getId();
        String id_str = objectMapper.writeValueAsString(id);
        given(escursioneService.getListEscursioniByIstruttore(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/escursione/getAll/byIstruttore/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(id_str))
                .andExpect(status().isInternalServerError());
    }

}
