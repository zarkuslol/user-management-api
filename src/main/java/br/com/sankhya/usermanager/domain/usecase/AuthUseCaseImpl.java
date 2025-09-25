package br.com.sankhya.usermanager.domain.usecase;

import br.com.sankhya.usermanager.domain.ports.inbound.AuthUseCase;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.LoginCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.LoginResponse;
import br.com.sankhya.usermanager.domain.ports.outbound.PasswordHasherPort;
import br.com.sankhya.usermanager.domain.ports.outbound.TokenServicePort;
import br.com.sankhya.usermanager.domain.ports.outbound.UserRepositoryPort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service // Marcamos como @Service para o Spring gerenciá-lo como um bean
public class AuthUseCaseImpl implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final TokenServicePort tokenServicePort;

    public AuthUseCaseImpl(UserRepositoryPort userRepositoryPort, PasswordHasherPort passwordHasherPort, TokenServicePort tokenServicePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
        this.tokenServicePort = tokenServicePort;
    }

    @Override
    public LoginResponse login(LoginCommand command) {
        var user = userRepositoryPort.findByUsername(command.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordHasherPort.check(command.password(), user.getPassword().hash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!user.isEnabled()) {
            throw new BadCredentialsException("User without access");
        }

        var token = tokenServicePort.generateToken(user);
        return new LoginResponse(token);
    }
}
