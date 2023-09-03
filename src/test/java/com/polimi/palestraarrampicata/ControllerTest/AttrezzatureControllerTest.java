package com.polimi.palestraarrampicata.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.request.RequestAttrezzatura;
import com.polimi.palestraarrampicata.dto.request.RequestNoleggiaAttrezzatura;
import com.polimi.palestraarrampicata.dto.response.ResponseAttrezzatura;
import com.polimi.palestraarrampicata.model.Attrezzatura;
import com.polimi.palestraarrampicata.model.Noleggio;
import com.polimi.palestraarrampicata.model.Taglia;
import com.polimi.palestraarrampicata.service.AttrezzaturaService;
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
public class AttrezzatureControllerTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
   private AttrezzaturaService attrezzaturaService;
    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutte le attrezzature disponibili.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll restituisca uno stato OK (HTTP 200 OK)
     * e che la risposta includa i dati corretti delle attrezzature.
     * Il mock attrezzaturaService è configurato per restituire una lista di attrezzature di test.
     * Viene eseguita la chiamata alla richiesta GET e vengono verificati lo stato HTTP e i dati delle attrezzature nella risposta.
     */

    @Test
    @WithMockUser()
    public void getAll_Attrezzatura_ReturnOk() throws Exception {
        List<ResponseAttrezzatura> responseAttrezzatura = Stub.getResponseAttrezzaturaList();
        given(attrezzaturaService.getListAttrezzaturaDisponibile()).willReturn(responseAttrezzatura);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("0")));

    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutte le attrezzature disponibili quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll restituisca uno stato di errore interno del server (HTTP 500 Internal Server Error).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 500 Internal Server Error.
     */
    @Test
    @WithMockUser()
    public void getAll_Attrezzatura_ReturnBadRequest_EntityNotFound() throws Exception {
        given(attrezzaturaService.getListAttrezzaturaDisponibile()).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll"))
                .andExpect(status().isInternalServerError());

    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutte le attrezzature di un determinato tipo.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll/type restituisca uno stato HTTP 200 OK.
     * Il mock attrezzaturaService è configurato per restituire una lista di attrezzature.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 200 OK
     * e che il corpo della risposta contenga i dettagli delle attrezzature.
     */
    @Test
    @WithMockUser()
    public void getAll_Type_Attrezzatura_ReturnOk() throws Exception{
        List<ResponseAttrezzatura> responseAttrezzatura = Stub.getResponseAttrezzaturaList();
        RequestAttrezzatura requestAttrezzatura = Stub.getRequestAttrezzaturaStub();
        String requestAttrezzatura_string = new ObjectMapper().writeValueAsString(requestAttrezzatura);
        given(attrezzaturaService.getListAttrezzaturaPerTipo(any())).willReturn(responseAttrezzatura);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/type/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAttrezzatura_string));

    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutte le attrezzature di un determinato tipo quando si verifica
     * un'eccezione EntityNotFoundException.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll/type restituisca uno stato di errore interno del server
     * (HTTP 500 Internal Server Error).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 500 Internal Server Error.
     */
    @Test
    @WithMockUser()
    public void getAll_Type_Attrezzatura_ReturnInternalServerError_EntityNotFound() throws Exception{
        RequestAttrezzatura requestAttrezzatura = Stub.getRequestAttrezzaturaStub();
        String requestAttrezzatura_string = new ObjectMapper().writeValueAsString(requestAttrezzatura);
        given(attrezzaturaService.getListAttrezzaturaPerTipo(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAttrezzatura_string))
                .andExpect(status().isInternalServerError());

    }

    /**
     * Testa il comportamento dell'endpoint per noleggiare un'attrezzatura.
     * Verifica che una richiesta POST a /api/v1/attrezzatura/noleggia restituisca uno stato di successo (HTTP 200 OK).
     * Il mock attrezzaturaService è configurato per restituire un'attrezzatura durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser()
    public  void noleggia_Attrezzatura_ReturnOk() throws Exception{
        RequestNoleggiaAttrezzatura requestNoleggiaAttrezzatura = Stub.getRequesNoleggioStub();
        Attrezzatura attrezzatura= Stub.getAttrezzoStub();
        String requestNoleggia_string = new ObjectMapper().writeValueAsString(requestNoleggiaAttrezzatura);
        given(attrezzaturaService.noleggiaAttrazzatura(any(), any())).willReturn(attrezzatura);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/attrezzatura/noleggia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestNoleggia_string))
                .andExpect(status().isOk());

    }

    /**
     * Testa il comportamento dell'endpoint per noleggiare un'attrezzatura.
     * Verifica che una richiesta POST a /api/v1/attrezzatura/noleggia restituisca uno stato di successo (HTTP 200 OK).
     * Il mock attrezzaturaService è configurato per restituire un'attrezzatura durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser()
    public  void noleggia_Attrezzatura_ReturnBadRequest_EntityNotFound() throws Exception{
        RequestNoleggiaAttrezzatura requestNoleggiaAttrezzatura = Stub.getRequesNoleggioStub();
        String requestNoleggia_string = new ObjectMapper().writeValueAsString(requestNoleggiaAttrezzatura);
        given(attrezzaturaService.noleggiaAttrazzatura(any(), any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/attrezzatura/noleggia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNoleggia_string))
                .andExpect(status().isBadRequest());

    }

    /**
     * Testa il comportamento dell'endpoint per noleggiare un'attrezzatura quando si verifica un'eccezione IllegalStateException.
     * Verifica che una richiesta POST a /api/v1/attrezzatura/noleggia restituisca uno stato di errore Bad Request (HTTP 400 Bad Request).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione IllegalStateException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser()
    public  void noleggia_Attrezzatura_ReturnBadRequest_IllegalStateException() throws Exception{
        RequestNoleggiaAttrezzatura requestNoleggiaAttrezzatura = Stub.getRequesNoleggioStub();
        String requestNoleggia_string = new ObjectMapper().writeValueAsString(requestNoleggiaAttrezzatura);
        given(attrezzaturaService.noleggiaAttrazzatura(any(), any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/attrezzatura/noleggia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNoleggia_string))
                .andExpect(status().isBadRequest());

    }

    /**
     * Testa il comportamento dell'endpoint per noleggiare un'attrezzatura quando si verifica un'eccezione DateTimeException.
     * Verifica che una richiesta POST a /api/v1/attrezzatura/noleggia restituisca uno stato di errore Bad Request (HTTP 400 Bad Request).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione DateTimeException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser()
    public  void noleggia_Attrezzatura_ReturnBadRequest_DataTime() throws Exception{
        RequestNoleggiaAttrezzatura requestNoleggiaAttrezzatura = Stub.getRequesNoleggioStub();
        String requestNoleggia_string = new ObjectMapper().writeValueAsString(requestNoleggiaAttrezzatura);
        given(attrezzaturaService.noleggiaAttrazzatura(any(), any())).willThrow(new DateTimeException("data non valida"));
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/attrezzatura/noleggia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNoleggia_string))
                .andExpect(status().isBadRequest());

    }

    /**
     * Testa il comportamento dell'endpoint per inserire un'attrezzatura con ruolo ADMIN.
     * Verifica che una richiesta POST a /api/v1/attrezzatura/inserisci restituisca uno stato OK (HTTP 200 OK).
     * Il mock attrezzaturaService è configurato per restituire un'attrezzatura.
     * Viene eseguita la chiamata alla richiesta POST e vengono verificati il codice di stato, l'id e il nome dell'attrezzatura.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void inserisciAtrezzatura_ReturnOk() throws  Exception{
        RequestAttrezzatura requestAttrezzatura = Stub.getRequestAttrezzaturaStub();
        Attrezzatura attrezzatura = Stub.getAttrezzoStub();
        String requestAttrazzatura_string = new ObjectMapper().writeValueAsString(requestAttrezzatura);
        given(attrezzaturaService.inserisciNuovoAttrezzo(any())).willReturn(attrezzatura);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/attrezzatura/inserisci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAttrazzatura_string))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(attrezzatura.getId().toString())))
                .andExpect(jsonPath("$.nomeAttrezzo",is(attrezzatura.getNomeAttrezzatura())));
    }

    /**
     * Testa il comportamento dell'endpoint per inserire un'attrezzatura quando si verifica un'eccezione EntityNotFoundException,
     * con ruolo ADMIN.
     * Verifica che una richiesta POST a /api/v1/attrezzatura/inserisci restituisca uno stato di errore BadRequest (HTTP 400 Bad Request).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void inserisciAtrezzatura_ReturBadRequest_EntityNotFound() throws  Exception{
        RequestAttrezzatura requestAttrezzatura = Stub.getRequestAttrezzaturaStub();
        String requestAttrazzatura_string = new ObjectMapper().writeValueAsString(requestAttrezzatura);
        given(attrezzaturaService.inserisciNuovoAttrezzo(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/attrezzatura/inserisci")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAttrazzatura_string))
                .andExpect(status().isBadRequest());

    }

    /**
     * Testa il comportamento dell'endpoint per inserire un'attrezzatura quando si verifica un'eccezione IllegalStateException,
     * con ruolo ADMIN.
     * Verifica che una richiesta POST a /api/v1/attrezzatura/inserisci restituisca uno stato di errore BadRequest (HTTP 400 Bad Request).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione IllegalStateException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void inserisciAtrezzatura_ReturBadRequest_IllegalStateException() throws  Exception{
        RequestAttrezzatura requestAttrezzatura = Stub.getRequestAttrezzaturaStub();
        String requestAttrazzatura_string = new ObjectMapper().writeValueAsString(requestAttrezzatura);
        given(attrezzaturaService.inserisciNuovoAttrezzo(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/attrezzatura/inserisci")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAttrazzatura_string))
                .andExpect(status().isBadRequest());

    }

    /**
     * Testa il comportamento dell'endpoint per inserire un'attrezzatura quando si verifica un'eccezione EntityNotFoundException,
     * senza specificare un ruolo.
     * Verifica che una richiesta POST a /api/v1/attrezzatura/inserisci restituisca uno stato di errore Forbidden (HTTP 403 Forbidden).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta POST e viene verificato che la risposta HTTP abbia uno stato 403 Forbidden.
     */
    @Test
    @WithMockUser()
    public void inserisciAtrezzatura_ReturForbidden() throws  Exception{
        RequestAttrezzatura requestAttrezzatura = Stub.getRequestAttrezzaturaStub();
        String requestAttrazzatura_string = new ObjectMapper().writeValueAsString(requestAttrezzatura);
        given(attrezzaturaService.inserisciNuovoAttrezzo(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/attrezzatura/inserisci")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAttrazzatura_string))
                .andExpect(status().isForbidden());

    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutti i noleggi di attrezzature quando la richiesta è valida.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll/noleggi restituisca uno stato di successo (HTTP 200 OK).
     * Il mock attrezzaturaService è configurato per restituire una lista di attrezzature con informazioni sui noleggi.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser()
    public void getAll_Noleggi_ReturnOk() throws Exception{
        List<Attrezzatura> getListAttrezzatura = Stub.getListAttrezziStub();
        List<Noleggio> getListNoleggio = Stub.getListNoleggioStub();
        List<Taglia> getListTaglia = Stub.getListTaglieStub();
        for(int i =0;i<getListAttrezzatura.size();i++){
            getListAttrezzatura.get(i).setNoleggi(getListNoleggio);
            getListAttrezzatura.get(i).setNomeTaglia(getListTaglia);
        }
        given(attrezzaturaService.getAllNoleggi()).willReturn(getListAttrezzatura);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/noleggi")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutti i noleggi di attrezzature quando si verifica un'eccezione EntityNotFoundException.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll/noleggi restituisca uno stato di errore bad request (HTTP 400 Bad Request).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione EntityNotFoundException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser()
    public void getALL_Noleggi_ReturnBadRequest_EntityNotFound() throws Exception{
        given(attrezzaturaService.getAllNoleggi()).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/noleggi")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutti i noleggi di attrezzature quando si verifica un'eccezione IllegalStateException.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll/noleggi restituisca uno stato di errore bad request (HTTP 400 Bad Request).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione IllegalStateException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser()
    public void getALL_Noleggi_ReturnBadRequest_IllegalState() throws Exception{
        given(attrezzaturaService.getAllNoleggi()).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/noleggi")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutti i noleggi di attrezzature quando si verifica un'eccezione DateTimeException.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll/noleggi restituisca uno stato di errore bad request (HTTP 400 Bad Request).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione DateTimeException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser()
    public void getAll_Noleggi_ReturnBadRequest_DateTime() throws Exception{
        given(attrezzaturaService.getAllNoleggi()).willThrow(new DateTimeException("data non valida"));
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/noleggi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutti i noleggi non finiti di attrezzature.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll/noleggi_non_finiti restituisca uno stato di successo (HTTP 200 OK).
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 200 OK.
     */
    @Test
    @WithMockUser()
    public void getAll_NoleggiNonFiniti_ReturnOk() throws Exception{
        List<Attrezzatura> getListAttrezzatura = Stub.getListAttrezziStub();
        List<Noleggio> getListNoleggio = Stub.getListNoleggioStub();
        List<Taglia> getListTaglia = Stub.getListTaglieStub();
        for(int i =0;i<getListAttrezzatura.size();i++){
            getListAttrezzatura.get(i).setNoleggi(getListNoleggio);
            getListAttrezzatura.get(i).setNomeTaglia(getListTaglia);
        }

        given(attrezzaturaService.getAllNoleggiNonFiniti()).willReturn(getListAttrezzatura);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/noleggi_non_finiti")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutti i noleggi non finiti di attrezzature quando si verifica un'eccezione IllegalStateException.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll/noleggi_non_finiti restituisca uno stato di errore bad request (HTTP 400 Bad Request).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione IllegalStateException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser()
    public void getAll_NoleggiNonFiniti_ReturnBad_IllegalState() throws Exception{
        given(attrezzaturaService.getAllNoleggiNonFiniti()).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/noleggi_non_finiti")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Testa il comportamento dell'endpoint per ottenere tutti i noleggi non finiti di attrezzature quando si verifica un'eccezione DateTimeException.
     * Verifica che una richiesta GET a /api/v1/attrezzatura/getAll/noleggi_non_finiti restituisca uno stato di errore bad request (HTTP 400 Bad Request).
     * Il mock attrezzaturaService è configurato per lanciare un'eccezione DateTimeException durante la richiesta.
     * Viene eseguita la chiamata alla richiesta GET e viene verificato che la risposta HTTP abbia uno stato 400 Bad Request.
     */
    @Test
    @WithMockUser()
    public void getAll_NoleggiNonFiniti_ReturnBad_DateTime() throws Exception{
        given(attrezzaturaService.getAllNoleggiNonFiniti()).willThrow(new DateTimeException("data inseritas non valida"));
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/noleggi_non_finiti")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }






}
