package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutorController.class)
class AutorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AutorRepository autores;

    @Test
    void deveCriarAutor() throws Exception {
        when(autores.save(any(Autor.class))).thenReturn(autor(1L, "Machado de Assis"));

        mvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Machado de Assis"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/autores/1"))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));
    }

    @Test
    void deveRetornar404ParaAutorInexistente() throws Exception {
        when(autores.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/autores/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarAutoresOrdenados() throws Exception {
        when(autores.findAllByOrderByNomeAsc()).thenReturn(List.of(
                autor(2L, "Clarice Lispector"), autor(1L, "Machado de Assis")));

        mvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Clarice Lispector"))
                .andExpect(jsonPath("$[1].nome").value("Machado de Assis"));
    }

    private Autor autor(Long id, String nome) {
        Autor autor = new Autor();
        autor.setId(id);
        autor.setNome(nome);
        return autor;
    }
}
