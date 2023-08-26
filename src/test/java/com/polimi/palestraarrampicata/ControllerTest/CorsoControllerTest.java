package com.polimi.palestraarrampicata.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polimi.palestraarrampicata.Stub;
import com.polimi.palestraarrampicata.dto.request.RequestValutazione;
import com.polimi.palestraarrampicata.mapping.Request;
import com.polimi.palestraarrampicata.model.Commento;
import com.polimi.palestraarrampicata.model.Corso;
import com.polimi.palestraarrampicata.model.Valutazione;
import com.polimi.palestraarrampicata.service.CorsoService;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CorsoControllerTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CorsoService corsoService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }
    ObjectMapper objectMapper = new ObjectMapper();
    @Test
    @WithMockUser(value = "spring", authorities = {"UTENTE"})
    public void givenCorso_CreaCorso_ReturnOk() throws Exception{
        Corso corso = Stub.getCorsoStub();
        String corso_string =  objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(corso);
        given(corsoService.creaCorso(any())).willReturn(corso);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/corso/crea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corso_string))
                        .andExpect(status().isOk());

    }


}
