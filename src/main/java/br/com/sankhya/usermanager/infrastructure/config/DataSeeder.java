package br.com.sankhya.usermanager.infrastructure.config;

import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.UserEntity;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaRoleRepository;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            JpaUserRepository userRepository,
            JpaRoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        String username = System.getenv("ADMIN_USER");
        String password = System.getenv("ADMIN_PASSWORD");

        return args -> {
            if (userRepository.findByUsername(username).isEmpty()) {

                // Busca a Role 'ADMIN' que já foi inserida pelo V1__init.sql
                roleRepository.findByName("ADMIN").ifPresent(adminRole -> {
                    System.out.println("Criando usuário administrador...");
                    UserEntity devUser = new UserEntity();
                    devUser.setUsername(username);
                    devUser.setPassword(passwordEncoder.encode(password));
                    devUser.setEmail("dev@example.com");
                    devUser.setEnabled(true);
                    devUser.setRole(adminRole);

                    userRepository.save(devUser);
                    System.out.println("Usuário 'dev' criado com sucesso.");
                });
            }
        };
    }
}
