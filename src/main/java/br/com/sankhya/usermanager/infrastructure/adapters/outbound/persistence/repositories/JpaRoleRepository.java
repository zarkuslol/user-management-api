package br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories;

import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(String name);
}
