package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin
public class AutorController {

    private final AutorRepository autores;

    public AutorController(AutorRepository autores) {
        this.autores = autores;
    }

    @PostMapping
    public ResponseEntity<Autor> criar(@Valid @RequestBody Autor dados) {
        Autor salvo = autores.save(dados);
        return ResponseEntity.created(URI.create("/api/autores/" + salvo.getId())).body(salvo);
    }

    @GetMapping
    public List<Autor> listar() {
        return autores.findAllByOrderByNomeAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> buscar(@PathVariable Long id) {
        return ResponseEntity.of(autores.findById(id));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Autor> atualizar(@PathVariable Long id, @Valid @RequestBody Autor dados) {
        Autor autor = buscarOuFalhar(id);
        autor.setNome(dados.getNome());
        autor.setNacionalidade(dados.getNacionalidade());
        autor.setDataNascimento(dados.getDataNascimento());
        return ResponseEntity.ok(autores.save(autor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Autor autor = buscarOuFalhar(id);
        try {
            autores.delete(autor);
        } catch (DataIntegrityViolationException excecao) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Exclua os livros deste autor antes de excluí-lo.", excecao);
        }
        return ResponseEntity.ok().build();
    }

    private Autor buscarOuFalhar(Long id) {
        return autores.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor não encontrado."));
    }
}
