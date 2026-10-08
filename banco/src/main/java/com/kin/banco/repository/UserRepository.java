package com.kin.banco.repository;

import com.kin.banco.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}