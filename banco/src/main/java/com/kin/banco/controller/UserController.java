
package com.kin.banco.controller;

import com.kin.banco.model.Users;
import com.kin.banco.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController extends ApiController<Users> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserRepository repository,
            PasswordEncoder passwordEncoder) {

        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<?> listUsers() {
        return super.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return super.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody Users user) {

        if (userRepository.existsByCpf(user.getCpf())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "status", 409,
                            "message", "CPF já cadastrado."
                    ));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "status", 409,
                            "message", "Email já cadastrado."
                    ));
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return super.create(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return super.delete(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Users user) {

        Optional<Users> optional = userRepository.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Users existing = optional.get();

        if (user.getName() != null) {
            existing.setName(user.getName());
        }

        if (user.getEmail() != null
                && !user.getEmail().equals(existing.getEmail())) {

            if (userRepository.existsByEmail(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", 200,
                                "message", "Email já cadastrado."
                        ));
            }

            existing.setEmail(user.getEmail());
        }

        if (user.getPhone() != null) {
            existing.setPhone(user.getPhone());
        }

        if (user.getPassword() != null
                && !user.getPassword().isBlank()) {

            existing.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );
        }

        existing.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(userRepository.save(existing));
    }
}