
package com.kin.banco.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public abstract class ApiController<T> {

    protected final JpaRepository<T, Long> repository;

    public ApiController(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    protected ResponseEntity<?> list() {
        try {
            List<T> data = repository.findAll();

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", 500,
                            "message", "Erro ao listar registros."
                    ));
        }
    }

    protected ResponseEntity<T> findById(Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    protected ResponseEntity<T> create(T entity) {
        T saved = repository.save(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    protected ResponseEntity<T> update(Long id, T entity) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(repository.save(entity));
    }

    protected ResponseEntity<Void> delete(Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}