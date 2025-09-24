package br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.mappers;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.vo.Email;
import br.com.sankhya.usermanager.domain.vo.PasswordHash;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User domain = new User();
        domain.setId(entity.getId());
        domain.setUsername(entity.getUsername());
        domain.setEmail(new Email(entity.getEmail()));
        domain.setPassword(new PasswordHash(entity.getPassword()));
        domain.setEnabled(entity.isEnabled());
        // TODO: Mapear a role quando a RoleEntity for criada
        return domain;
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail().address());
        entity.setPassword(domain.getPassword().hash());
        entity.setEnabled(domain.isEnabled());
        // TODO: Mapear a role quando a RoleEntity for criada
        return entity;
    }
}
