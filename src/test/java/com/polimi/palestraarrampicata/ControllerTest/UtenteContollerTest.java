package com.polimi.palestraarrampicata.ControllerTest;

import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.request.RequestCommento;
import com.polimi.palestraarrampicata.dto.response.ResponseLezione;
import com.polimi.palestraarrampicata.model.Commento;
import com.polimi.palestraarrampicata.model.Lezione;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.service.UtenteService;
import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @Test
    @WithMockUser
    public void creaCommento()throws Exception{
        RequestCommento requestCommento = Stub.getRequestCommento();
        Utente utente = Stub.getUtenteStub();
        Utente istruttore = Stub.getInstructorStub();
        Commento commento = Stub.getCommentiFiglioStub();
        commento.setCommentatore(utente);
        List<Commento> commenti = new ArrayList<>();
        commenti.add(commento);
        utente.setCommenti(commenti);
        commento.setIstruttoreCommentato(istruttore);
        given(utenteService.creaCommento(any(), any())).willReturn(commento);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/commenta/istruttore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("2")))
                .andExpect(jsonPath("$.istruttoreCommentato", is(istruttore)))
                .andExpect(jsonPath("$.dataInserimento", is(Utils.formatterDataTime("15/05/2023 12:00"))));

    }

}

