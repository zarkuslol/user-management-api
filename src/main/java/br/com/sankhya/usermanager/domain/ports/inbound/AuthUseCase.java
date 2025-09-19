package br.com.sankhya.usermanager.domain.ports.inbound;

import br.com.sankhya.usermanager.domain.ports.inbound.dtos.LoginCommand;

public interface AuthUseCase {
    /**
     * Authenticates a user and returns a token.
     * @param command The login credentials.
     * @return A JWT token string.
     */
    String login(LoginCommand command);
}
