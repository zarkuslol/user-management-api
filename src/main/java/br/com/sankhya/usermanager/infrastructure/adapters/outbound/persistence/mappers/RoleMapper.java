package br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.mappers;

import br.com.sankhya.usermanager.domain.model.Role;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        Role domain = new Role();
        // O ID na RoleEntity é Integer, mas no nosso domínio é Long
        domain.setId(entity.getId().longValue());
        domain.setName(entity.getName());
        return domain;
    }

    public RoleEntity toEntity(Role domain) {
        if (domain == null) {
            return null;
        }
        RoleEntity entity = new RoleEntity();
        if (domain.getId() != null) {
            // O ID na Role do domínio é Long, mas na entidade é Integer
            entity.setId(domain.getId().intValue());
        }
        entity.setName(domain.getName());
        return entity;
    }
}
