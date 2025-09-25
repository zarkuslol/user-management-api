package br.com.sankhya.usermanager.domain.ports.inbound;

import br.com.sankhya.usermanager.domain.ports.inbound.dtos.ForgotPasswordCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.LoginCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.LoginResponse;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.ResetPasswordCommand;

public interface AuthUseCase {
    /**
     * Authenticates a user and returns a token response.
     * @param command The login credentials.
     * @return A response object containing the JWT.
     */
    LoginResponse login(LoginCommand command);
}
