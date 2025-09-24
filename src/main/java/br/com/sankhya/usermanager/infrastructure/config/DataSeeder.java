package br.com.sankhya.usermanager.infrastructure.configuration;

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

        return args -> {
            if (userRepository.findByUsername("dev").isEmpty()) {

                // Busca a Role 'ADMIN' que já foi inserida pelo V1__init.sql
                roleRepository.findByName("ADMIN").ifPresent(adminRole -> {
                    System.out.println("Criando usuário de desenvolvimento 'dev'...");
                    UserEntity devUser = new UserEntity();
                    devUser.setUsername("dev");
                    devUser.setPassword(passwordEncoder.encode("dev123"));
                    devUser.setEmail("dev@example.com");
                    devUser.setEnabled(true);
                    devUser.setRole(adminRole); // <-- A LINHA QUE FALTAVA!

                    userRepository.save(devUser);
                    System.out.println("Usuário 'dev' criado com sucesso.");
                });
            }
        };
    }
}
