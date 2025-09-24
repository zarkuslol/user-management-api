package br.com.sankhya.usermanager.domain.ports.outbound;

import br.com.sankhya.usermanager.domain.model.Role;
import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(String name);
}
