package com.dev.quikkkk.auth_service.repository;

import com.dev.quikkkk.auth_service.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserCredentialsRepository extends JpaRepository<UserCredentials, String> {
    Optional<UserCredentials> findByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<UserCredentials> findByEmail(String email);
}
