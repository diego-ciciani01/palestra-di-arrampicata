package com.polimi.palestraarrampicata.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.request.RequestAttrezzatura;
import com.polimi.palestraarrampicata.dto.request.RequestNoleggiaAttrezzatura;
import com.polimi.palestraarrampicata.dto.response.ResponseAttrezzatura;
import com.polimi.palestraarrampicata.model.Attrezzatura;
import com.polimi.palestraarrampicata.service.AttrezzaturaService;
import com.polimi.palestraarrampicata.service.UtenteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
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


    @Test
    @WithMockUser()
    public void getAll_Attrezzatura_ReturnOk() throws Exception {
        List<ResponseAttrezzatura> responseAttrezzatura = Stub.getResponseAttrezzaturaList();
        given(attrezzaturaService.getListAttrezzaturaDisponibile()).willReturn(responseAttrezzatura);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("0")));

    }
    @Test
    @WithMockUser()
    public void getAll_Attrezzatura_ReturnBadRequest_EntityNotFound() throws Exception {
        given(attrezzaturaService.getListAttrezzaturaDisponibile()).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll"))
                .andExpect(status().isInternalServerError());

    }
    @Test
    @WithMockUser()
    public void getAll_Type_Attrezzatura_ReturnOk() throws Exception{
        List<ResponseAttrezzatura> responseAttrezzatura = Stub.getResponseAttrezzaturaList();
        RequestAttrezzatura requestAttrezzatura = Stub.getRequestAttrezzaturaStub();
        String requestAttrezzatura_string = new ObjectMapper().writeValueAsString(requestAttrezzatura);
        given(attrezzaturaService.getListAttrezzaturaPerTipo(any())).willReturn(responseAttrezzatura);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/type/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAttrezzatura_string))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseAttrezzatura.get(0).getId())));

    }
    @Test
    @WithMockUser()
    public void getAll_Type_Attrezzatura_ReturnInternalServerError_EntityNotFound() throws Exception{
        RequestAttrezzatura requestAttrezzatura = Stub.getRequestAttrezzaturaStub();
        String requestAttrezzatura_string = new ObjectMapper().writeValueAsString(requestAttrezzatura);
        given(attrezzaturaService.getListAttrezzaturaPerTipo(any())).willThrow(new EntityNotFoundException());
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/type/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAttrezzatura_string))
                .andExpect(status().isInternalServerError());

    }
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
    @Test
    @WithMockUser()
    public void getAll_Noleggi_ReturnOk() throws Exception{
        List<Attrezzatura> attrezi = Stub.getListAttrezziStub();
        given(attrezzaturaService.getAllNoleggi()).willReturn(attrezi);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/attrezzatura/getAll/noleggi")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }



}
