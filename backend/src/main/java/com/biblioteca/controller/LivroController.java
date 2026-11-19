package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    public ResponseEntity<Livro> criar(@Valid @RequestBody DadosLivro dados) {
        Livro salvo = livros.save(preencher(new Livro(), dados));
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

    @GetMapping("/autor/{autorId}")
    public List<Livro> listarPorAutor(@PathVariable Long autorId) {
        return livros.findByAutorIdOrderByTituloAsc(autorId);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @Valid @RequestBody DadosLivro dados) {
        Livro livro = buscarOuFalhar(id);
        return ResponseEntity.ok(livros.save(preencher(livro, dados)));
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

    private Livro preencher(Livro livro, DadosLivro dados) {
        Autor autor = autores.findById(dados.autor().id()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Autor não encontrado."));
        livro.setTitulo(dados.titulo());
        livro.setIsbn(dados.isbn());
        livro.setAnoPublicacao(dados.anoPublicacao());
        livro.setGenero(dados.genero());
        livro.setAutor(autor);
        return livro;
    }

    public record DadosLivro(
            @NotBlank @Size(max = 200) String titulo,
            @Size(max = 20) String isbn,
            Integer anoPublicacao,
            @Size(max = 50) String genero,
            @NotNull @Valid ReferenciaAutor autor
    ) {}

    public record ReferenciaAutor(@NotNull Long id) {}
}
