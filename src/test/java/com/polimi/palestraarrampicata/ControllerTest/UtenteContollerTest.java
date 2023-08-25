package com.polimi.palestraarrampicata.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.DTOManager;
import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.request.RequestRegistrazione;
import com.polimi.palestraarrampicata.dto.request.RequestValutazione;
import com.polimi.palestraarrampicata.dto.response.ResponseCommento;
import com.polimi.palestraarrampicata.dto.response.ResponseLezione;
import com.polimi.palestraarrampicata.dto.response.ResponseValutazione;
import com.polimi.palestraarrampicata.exception.CreazioneCommentoFallita;
import com.polimi.palestraarrampicata.exception.InserimentoValutazioneFallita;
import com.polimi.palestraarrampicata.mapping.Request;
import com.polimi.palestraarrampicata.mapping.Response;
import com.polimi.palestraarrampicata.model.Commento;
import com.polimi.palestraarrampicata.model.Lezione;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
        List<ResponseLezione> Responselezioni = Stub.getLezioniResponseStubAccettate();
        given(utenteService.getListInvitiLezioneAccettate(any())).willReturn(Responselezioni);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/getAll/Inviti/accettati"))
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
     *  Questo metodo testa la chiamata al endpoint commenta istruttore
     *  viene preso un oggetto commento e un oggetto response commento dallo stub,
     *  vengono settati poi dei parametri
     * @throws Exception
     */
    @Test
    @WithMockUser
    public void creaCommento_returnOk()throws Exception{
        Commento commento = Stub.getCommentiFiglioStub();
        Utente utente = Stub.getUtenteStub();
        RequestCommento requestCommento = Stub.getRequestCommento();
        ResponseCommento responseCommento = new ResponseCommento();
        responseCommento.setId("2");
        responseCommento.setTesto(requestCommento.getTesto());
        String requestCommento_asString = new ObjectMapper().writeValueAsString(requestCommento);
        given(utenteService.creaCommento(any(), any())).willReturn(commento);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/commenta/istruttore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestCommento_asString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseCommento.getId())));
    }
    @Test
    @WithMockUser
    public  void creaCommento_returnBadRequest()throws Exception{
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
                        //.header("Authorization", Stub.getJwtStub_User())
                        .content(requestValutazione_asString))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(Integer.parseInt(responseValutazione.getId()))))
                        .andExpect(jsonPath("$.valore", is(Integer.parseInt(responseValutazione.getValore()))));


    }
    @Test
    @WithMockUser(value = "spring", authorities = {"UTENTE"})
    public  void creaValutazione_returnBadRequest()throws Exception{
        Valutazione valutazione = Stub.getValutazioneStub();
        RequestValutazione requestValutazione = Request.toRequestValutazioneByValutazioneMapper(valutazione);
        requestValutazione.setEmailValutato(null);
        given(utenteService.creaValutazione( any(), any())).willThrow((new InserimentoValutazioneFallita("inserimento valutazione fallita")));
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/valuta/istruttore"))
                .andExpect(status().isInternalServerError());

    }

}

