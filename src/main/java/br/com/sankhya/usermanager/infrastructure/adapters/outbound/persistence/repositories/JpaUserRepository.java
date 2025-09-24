package br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories;

import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Page<UserEntity> findByEnabled(boolean enabled, Pageable pageable);
}
