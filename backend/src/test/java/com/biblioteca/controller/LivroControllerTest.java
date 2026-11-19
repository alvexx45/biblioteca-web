package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
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

@WebMvcTest(LivroController.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LivroRepository livros;

    @MockBean
    private AutorRepository autores;

    @Test
    void deveCriarLivro() throws Exception {
        Autor machado = autor(1L, "Machado de Assis");
        when(autores.findById(1L)).thenReturn(Optional.of(machado));
        when(livros.save(any(Livro.class))).thenReturn(livro(1L, "Dom Casmurro", machado));

        mvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Dom Casmurro","anoPublicacao":1899,"autor":{"id":1}}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/livros/1"))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor.nome").value("Machado de Assis"));
    }

    @Test
    void deveRetornar404ParaLivroInexistente() throws Exception {
        when(livros.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/livros/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRecusarLivroComAutorInexistente() throws Exception {
        when(autores.findById(42L)).thenReturn(Optional.empty());

        mvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Livro Sem Autor","autor":{"id":42}}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarLivrosOrdenados() throws Exception {
        Autor machado = autor(1L, "Machado de Assis");
        when(livros.findAllByOrderByTituloAsc()).thenReturn(List.of(
                livro(2L, "Dom Casmurro", machado), livro(1L, "Memórias Póstumas", machado)));

        mvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[1].titulo").value("Memórias Póstumas"));
    }

    private Autor autor(Long id, String nome) {
        Autor autor = new Autor();
        autor.setId(id);
        autor.setNome(nome);
        return autor;
    }

    private Livro livro(Long id, String titulo, Autor autor) {
        Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        return livro;
    }
}
