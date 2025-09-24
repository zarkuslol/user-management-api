package br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence;

import br.com.sankhya.usermanager.domain.model.Role;
import br.com.sankhya.usermanager.domain.ports.outbound.RoleRepositoryPort;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.mappers.RoleMapper;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaRoleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final JpaRoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleRepositoryAdapter(JpaRoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name).map(roleMapper::toDomain);
    }
}
