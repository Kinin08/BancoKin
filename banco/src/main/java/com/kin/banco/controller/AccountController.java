
package com.kin.banco.controller;

import com.kin.banco.model.Account;
import com.kin.banco.model.Users;
import com.kin.banco.repository.AccountRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController extends ApiController<Account> {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountController(
            AccountRepository repository,
            PasswordEncoder passwordEncoder) {

        super(repository);
        this.accountRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<?> listAccount() {
        return super.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return super.findById(id);
    }
    @PostMapping
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody Account account) {

        if (accountRepository.existsByAccountNumber(
                account.getAccountNumber())) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "status", 409,
                            "message", "Número da conta já existe."
                    ));
        }

        account.setBalance(BigDecimal.ZERO);

        return super.create(account);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        return super.delete(id);
    }

}