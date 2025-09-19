package br.com.sankhya.usermanager.domain.ports.outbound;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.vo.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

/**
 * Port para operações de persistência da entidade User.
 * Define o contrato que a camada de domínio espera para interagir com o banco de dados.
 */
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(Email email);
    Page<User> findAll(Pageable pageable);
    Page<User> findAllByEnabled(boolean enabled, Pageable pageable);
    void deleteById(Long id);
}
