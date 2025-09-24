package br.com.sankhya.usermanager.infrastructure.config;

import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.UserEntity;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaRoleRepository;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Value("${admin.user.name}")
    private String username;

    @Value("${admin.user.password}")
    private String password;

    @Bean
    CommandLineRunner initDatabase(
            JpaUserRepository userRepository,
            JpaRoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (userRepository.findByUsername(this.username).isEmpty()) {

                // Busca a Role 'ADMIN' que já foi inserida pelo V1__init.sql
                roleRepository.findByName("ADMIN").ifPresent(adminRole -> {
                    System.out.println("Criando usuário administrador...");
                    UserEntity devUser = new UserEntity();
                    devUser.setUsername(this.username);
                    devUser.setPassword(passwordEncoder.encode(this.password));
                    devUser.setEmail("dev@example.com");
                    devUser.setEnabled(true);
                    devUser.setRole(adminRole);

                    userRepository.save(devUser);
                    System.out.println("Usuário administrador criado com sucesso.");
                });
            }
        };
    }
}
