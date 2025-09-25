package br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest;

import br.com.sankhya.usermanager.domain.ports.inbound.AuthUseCase;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.LoginCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.LoginResponse;
import br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoint para autenticação de usuários e obtenção de token JWT.")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica um usuário", description = "Recebe as credenciais (username e password) e retorna um token JWT em caso de sucesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: campos em branco)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas (usuário, senha ou usuário inativo)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginCommand command) {
        var response = authUseCase.login(command);
        return ResponseEntity.ok(response);
    }
}
