package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin
public class LivroController {

    private final LivroRepository livros;
    private final AutorRepository autores;

    public LivroController(LivroRepository livros, AutorRepository autores) {
        this.livros = livros;
        this.autores = autores;
    }

    @PostMapping
    public ResponseEntity<Livro> criar(@Valid @RequestBody Livro dados) {
        dados.setId(null);
        dados.setAutor(resolverAutor(dados.getAutor()));
        Livro salvo = livros.save(dados);
        return ResponseEntity.created(URI.create("/api/livros/" + salvo.getId())).body(salvo);
    }

    @GetMapping
    public List<Livro> listar() {
        return livros.findAllByOrderByTituloAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscar(@PathVariable Long id) {
        return ResponseEntity.of(livros.findById(id));
    }

    // (extra) livros de um autor
    @GetMapping("/autor/{autorId}")
    public List<Livro> listarPorAutor(@PathVariable Long autorId) {
        return livros.findByAutorIdOrderByTituloAsc(autorId);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @Valid @RequestBody Livro dados) {
        Livro livro = buscarOuFalhar(id);
        livro.setTitulo(dados.getTitulo());
        livro.setIsbn(dados.getIsbn());
        livro.setAnoPublicacao(dados.getAnoPublicacao());
        livro.setGenero(dados.getGenero());
        livro.setAutor(resolverAutor(dados.getAutor()));
        return ResponseEntity.ok(livros.save(livro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        livros.delete(buscarOuFalhar(id));
        return ResponseEntity.ok().build();
    }

    private Livro buscarOuFalhar(Long id) {
        return livros.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado."));
    }

    /** O JSON traz só {"id": n}; aqui buscamos o autor completo (e recusamos id inexistente). */
    private Autor resolverAutor(Autor referencia) {
        if (referencia.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o id do autor.");
        }
        return autores.findById(referencia.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Autor não encontrado."));
    }
}
