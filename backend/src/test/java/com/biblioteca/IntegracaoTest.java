package com.biblioteca;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegracaoTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void deveVincularLivroAAutor() throws Exception {
        long autorId = criarAutor("Machado de Assis");

        mvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Dom Casmurro","isbn":"978-8535910667",
                                 "anoPublicacao":1899,"genero":"Romance","autor":{"id":%d}}
                                """.formatted(autorId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.autor.id").value(autorId))
                .andExpect(jsonPath("$.autor.nome").value("Machado de Assis"));

        mvc.perform(get("/api/livros/autor/" + autorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"));
    }

    @Test
    void deveExecutarFluxoCompletoDeAutor() throws Exception {
        long id = criarAutor("Clarice Lispector");

        mvc.perform(get("/api/autores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Clarice Lispector"));

        mvc.perform(put("/api/autores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Clarice L.\",\"nacionalidade\":\"Brasileira\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Clarice L."));

        mvc.perform(delete("/api/autores/" + id)).andExpect(status().isOk());
        mvc.perform(get("/api/autores/" + id)).andExpect(status().isNotFound());
    }

    private long criarAutor(String nome) throws Exception {
        String resposta = mvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"" + nome + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(resposta, "$.id")).longValue();
    }
}
