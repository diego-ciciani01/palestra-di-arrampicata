package com.polimi.palestraarrampicata.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestIscrivitiPalestra;
import com.polimi.palestraarrampicata.dto.request.RequestPalestra;
import com.polimi.palestraarrampicata.dto.response.ResponsePalestra;
import com.polimi.palestraarrampicata.dto.response.ResponseUtente;
import com.polimi.palestraarrampicata.mapping.Request;
import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Palestra;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.service.PalestraService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@RequiredArgsConstructor
@RunWith(SpringRunner.class)
public class PalestraControllerTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private PalestraService palestraService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }
    private ObjectMapper  objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public  void creaPalestra_ReturnOk() throws Exception{
        Palestra palestra = Stub.getPalestraStub();
        RequestPalestra palestraRequest = Request.toRequestPalestraByPalestraMapper(palestra);
        String request_str =  new ObjectMapper().writeValueAsString(palestraRequest);
        given(palestraService.createPalestra(any())).willReturn(palestra);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/palestra/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request_str))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(palestra.getId().toString())));

    }

    /**
     *  Questo metodo testa la chiamata al endpoint creaPalestra di una palestra
     *  Il metodo può essere invocato solo da ADMIN e per testare che questo sia vero non setto il @WithMockUser, così
     *  considera la chiamata come se fosse fatta da un utente qualsiasi.
     *  Viene successivamente effettuata un chiamata Http di tipo Post all'indirizzo
     *  "/api/v1/user/palestra/create/" passo un di cap sbagliato alla richiesta
     *   Infine, viene controllato l'oggetto di risposta e confrontato con lo status (403 Forbidden).
     * @throws Exception
     */
    @Test
    @WithMockUser()
    public void creaPalestra_ReturnIsForbidden() throws  Exception{
        Palestra palestra = Stub.getPalestraStub();
        palestra.setCap("0000000");
        RequestPalestra palestraRequest = Request.toRequestPalestraByPalestraMapper(palestra);
        String request_str =  new ObjectMapper().writeValueAsString(palestraRequest);
        given(palestraService.createPalestra(any())).willThrow(new  IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/palestra/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request_str))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "spring", authorities = {"ADMIN"})
    public void creaPalestra_ReturnBadRequest_IllegalStateException() throws  Exception{
        Palestra palestra = Stub.getPalestraStub();
        palestra.setId(23);
        RequestPalestra palestraRequest = Request.toRequestPalestraByPalestraMapper(palestra);
        String request_str =  new ObjectMapper().writeValueAsString(palestraRequest);
        given(palestraService.createPalestra(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/palestra/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request_str))
                        .andExpect(status().isBadRequest());
    }

    /**
     * Questo metodo testa la chiamata all'endpoint getAll di palestra, che permette ottenere una lista di
     * tutte le palestre salvate nel db, il metodo può essere invocato da tutti gli utenti della piattaforma
     * indistintamente dal ruolo che ricoprono
     * Viene mockato il dato di una lista risposta di tipo "ResponseCommento"
     * in fine viene effettuata la chiamata posta all'end point, dove ci aspettiamo che ritorni 200 ok
     * @throws Exception
     */
    @Test
    @WithMockUser()
    public void getAll_Palestra_ReturOk() throws  Exception{
        List<ResponsePalestra> responsePalestraList = Stub.getListResponsePalestraStub();
        given(palestraService.getAllPalestre()).willReturn(responsePalestraList);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/palestra/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responsePalestraList.get(0).getId())))
                .andExpect(jsonPath("$[0].nome", is(responsePalestraList.get(0).getNome())));

    }

    /**
     * Questo metodo testa la chiamata all'endpoint getAll di palestra, che permette ottenere una lista di
     * tutte le palestre salvate nel db, il metodo può essere invocato da tutti gli utenti della piattaforma
     * da questo test ci aspettiamo che sollevi una eccezione.
     * non passando niente alla richiesta, l'eccezione che ci aspettiamo è EntityNotFoundException
     * portando il nostro codice a ritornare correttamente, come previsto da test 400 BadRequest
     * @throws Exception
     */

    @Test
    @WithMockUser()
    public void getAll_Palestra_ReturnBadRequest_EntityNotFound() throws  Exception{
        given(palestraService.getAllPalestre()).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/palestra/getAll"))
                .andExpect(status().isBadRequest());

    }
    @Test
    @WithMockUser()
    public void getAll_Utenti_ByPalestra_ReturnOk() throws Exception{
        Palestra palestra = Stub.getPalestraStub();
        List<Corso> corsoList = Stub.getListCorsoStub();
        List<Utente> utenti = Stub.getListUtentiStub();
        for(int i=0; i<utenti.size();i++) {
            utenti.get(i).setCorsiIscritto(corsoList);
            utenti.get(i).setIscrittiPalestra(palestra);
        }
        List<ResponseUtente>  responseUtenteList = DTOManager.toUserResponseByUsers(utenti);

        String utentiString = objectMapper.writeValueAsString(responseUtenteList);
        given(palestraService.getAllIscrittiBYEmailPalestra(any())).willReturn(utenti);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/palestra/getAll/iscrittiByPalestra/"+ palestra.getEmailPalestra()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(utentiString));

    }


    @Test
    @WithMockUser()
    public void getAll_Utenti_ByPalestra_ReturnBadRequest_IllegalState()throws Exception{
        Palestra palestra = Stub.getPalestraStub();
        palestra.setEmailPalestra("email non esistente");
        List<Corso> corsoList = Stub.getListCorsoStub();
        List<Utente> utenti = Stub.getListUtentiStub();

        for(int i=0; i<utenti.size();i++) {
            utenti.get(i).setCorsiIscritto(corsoList);
            utenti.get(i).setIscrittiPalestra(palestra);
        }

        given(palestraService.getAllIscrittiBYEmailPalestra(any())).willThrow(new IllegalStateException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/palestra/getAll/iscrittiByPalestra/"+ palestra.getEmailPalestra()))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser()
    public void getAll_Utenti_ByPalestra_ReturnBadRequest_EntityNotFound()throws Exception{
        Palestra palestra = Stub.getPalestraStub();
        palestra.setEmailPalestra("error email");
        List<Corso> corsoList = Stub.getListCorsoStub();
        List<Utente> utenti = Stub.getListUtentiStub();
        for(int i=0; i<utenti.size();i++) {
            utenti.get(i).setCorsiIscritto(corsoList);
            utenti.get(i).setIscrittiPalestra(palestra);
        }
        List<ResponseUtente>  responseUtenteList = DTOManager.toUserResponseByUsers(utenti);

        String utentiString = objectMapper.writeValueAsString(responseUtenteList);

        String msg = "la palestra cercata non esiste";
        given(palestraService.getAllIscrittiBYEmailPalestra(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/palestra/getAll/iscrittiByPalestra/"+ palestra.getEmailPalestra())
                .contentType(MediaType.APPLICATION_JSON)
                .content(utentiString))
                .andExpect(status().isBadRequest());

    }

    /**
     * Questo metodo testa la chiamata all'endpoint disiscrivi di palestra, che permette di disiscrivere un utente
     * dalla palestra, passando l'email nell'indirizzo della chiamata
     * in fine viene effettuata la chiamata post all'end point, dove ci aspettiamo che ritorni 200 ok
     */
    @Test
    @WithMockUser()
    public void disiscrivi_Utente_ReturnOk() throws Exception{
        Utente utente = Stub.getUtenteStub();
        String msg = "utente " + utente.getEmail() + " disiscritto correttamente";
        given(palestraService.disiscriviUtente(any())).willReturn(msg);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/palestra/disiscrivi/" + utente.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(utente.getEmail()))
                .andExpect(status().isOk());

    }


    /**
     * Questo metodo testa la chiamata all'endpoint disiscrivi di palestra, che permette di disiscrivere un utente
     * dalla palestra, non passiamo l'oggetto nella chiamata, in modo da sollevare un eccezione
     * in fine viene effettuata la chiamata post all'end point, dove ci aspettiamo che ritorni 200 ok
     * poortando il nostro codice a ritornare correttamente, come previsto da test 400 BadRequest
     */
    @Test
    @WithMockUser()
    public void disiscrivi_Utente_ReturnBadRequest_EntityNotFound () throws Exception{
        Utente utente = Stub.getUtenteStub();
        String msg = "utente " + utente.getEmail() + " disiscritto correttamente";
        given(palestraService.disiscriviUtente(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/palestra/disiscrivi/" + utente.getEmail()))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser()
    public void iscrivi_Utente_ReturnOk() throws Exception{
        String msg = "utente iscritto correttamente";
        Palestra palestra = Stub.getPalestraStub();
        RequestIscrivitiPalestra requestIscrivitiPalestra = Request.toRequestIscrivitiPalestrabyIscrivitiPalestraMapper(palestra);
        String requestIscrittiPalestra_string = new ObjectMapper().writeValueAsString(requestIscrivitiPalestra);
        given(palestraService.iscriviUtentePalestra(any(), any())).willReturn(msg);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/palestra/iscriviti")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestIscrittiPalestra_string))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser()
    public void iscrivi_Utente_ReturnBadRequest_EntityNotFound() throws Exception{
        given(palestraService.iscriviUtentePalestra(any(), any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/palestra/iscriviti"))
                .andExpect(status().isBadRequest());

    }



}
