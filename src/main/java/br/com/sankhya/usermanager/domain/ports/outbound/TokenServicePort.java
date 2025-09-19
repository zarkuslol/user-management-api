package br.com.sankhya.usermanager.domain.ports.outbound;

import br.com.sankhya.usermanager.domain.model.User;

/**
 * Port para gerenciamento de tokens de autenticação (JWT).
 */
public interface TokenServicePort {

    /**
     * Gera um token para um determinado usuário.
     * @param user O usuário para o qual o token será gerado.
     * @return O token JWT como uma String.
     */
    String generateToken(User user);

    /**
     * Extrai o nome de usuário (subject) de um token JWT.
     * @param token O token JWT.
     * @return O nome de usuário contido no token.
     */
    String getUsernameFromToken(String token);
}
