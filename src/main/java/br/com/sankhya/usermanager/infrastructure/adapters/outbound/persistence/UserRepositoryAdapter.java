package br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.outbound.UserRepositoryPort;
import br.com.sankhya.usermanager.domain.vo.Email;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.mappers.UserMapper;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository userRepository;
    private final UserMapper userMapper;

    public UserRepositoryAdapter(JpaUserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        var userEntity = userMapper.toEntity(user);
        var savedEntity = userRepository.save(userEntity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userRepository.findByEmail(email.address()).map(userMapper::toDomain);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDomain);
    }

    @Override
    public Page<User> findAllByEnabled(boolean enabled, Pageable pageable) {
        return userRepository.findByEnabled(enabled, pageable).map(userMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
