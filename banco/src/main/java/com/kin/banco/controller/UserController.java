package com.kin.banco.controller;

import com.kin.banco.model.Users;
import com.kin.banco.repository.UserRepository;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserRepository repository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers() {

        List<Users> users = repository.findAll();

        if (users.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", 404,
                            "message", "Nenhum usuário encontrado."
                    ));
        }

        List<Map<String, Object>> data = new ArrayList<>();

        for (Users user : users) {
            Map<String, Object> userData = new HashMap<>();

            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("phone", user.getPhone());
            userData.put("createdAt", user.getCreatedAt());

            data.add(userData);
        }

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "Usuários encontrados",
                        "data", data
                )
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(
            @PathVariable Long id) {

        Optional<Users> user = repository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", 404,
                            "message", "Usuário não existe."
                    ));
        }

        Users existingUser = user.get();

        Map<String, Object> data = new HashMap<>();

        data.put("id", existingUser.getId());
        data.put("name", existingUser.getName());
        data.put("email", existingUser.getEmail());
        data.put("phone", existingUser.getPhone());
        data.put("createdAt", existingUser.getCreatedAt());

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "Usuário encontrado",
                        "data", data
                )
        );
    }
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody Users user) {

        if (repository.existsByCpf(user.getCpf())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "status", 409,
                            "message","CPF já cadastrado."
                            ));
        }

        if (repository.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "status", 409,
                            "message","Email já cadastrado."
                    ));
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        Users newUser = repository.save(user);

        Map<String, Object> data = new HashMap<>();

        data.put("id", newUser.getId());
        data.put("name", newUser.getName());
        data.put("email", newUser.getEmail());
        data.put("phone", newUser.getPhone());
        data.put("createdAt", newUser.getCreatedAt());

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "Usuário criado",
                        "data", data
                )
        );

    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {

        Optional<Users> user = repository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", 404,
                            "message", "Usuário não existe."
                    ));
        }

        repository.deleteById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", 200,
                        "message", "Usuário deletado"
                ));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody Users user) {

        Optional<Users> optionalUser = repository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", 404,
                            "message", "Usuário não existe."
                    ));
        }

        Users existingUser = optionalUser.get();

        boolean updated = false;

        if (user.getName() != null &&
                !user.getName().equals(existingUser.getName())) {

            existingUser.setName(user.getName());
            updated = true;
        }

        if (user.getEmail() != null &&
                !user.getEmail().equals(existingUser.getEmail())) {

            if (repository.existsByEmail(user.getEmail())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", 409,
                                "message", "Email já cadastrado."
                        ));
            }

            existingUser.setEmail(user.getEmail());
            updated = true;
        }

        if (user.getPhone() != null &&
                !user.getPhone().equals(existingUser.getPhone())) {

            existingUser.setPhone(user.getPhone());
            updated = true;
        }
        if (user.getPassword() != null &&
                !user.getPassword().isBlank()) {

            if (!passwordEncoder.matches(
                    user.getPassword(),
                    existingUser.getPassword()
            )) {

                existingUser.setPassword(
                        passwordEncoder.encode(user.getPassword())
                );

                updated = true;
            }
        }

        if (!updated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of(
                            "status", 200,
                            "message", "Usuário sem atualizações"
                    ));

        }


        existingUser.setUpdatedAt(LocalDateTime.now());

        Users updatedUser = repository.save(existingUser);

        Map<String, Object> data = new HashMap<>();

        data.put("id", updatedUser.getId());
        data.put("name", updatedUser.getName());
        data.put("email", updatedUser.getEmail());
        data.put("phone", updatedUser.getPhone());
        data.put("createdAt", updatedUser.getCreatedAt());

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "Usuário atualizado",
                        "data", data
                )
        );
    }
}