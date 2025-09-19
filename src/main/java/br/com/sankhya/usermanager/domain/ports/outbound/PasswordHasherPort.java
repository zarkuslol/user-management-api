package br.com.sankhya.usermanager.domain.ports.outbound;

/**
 * Port para operações de hashing de senha.
 */
public interface PasswordHasherPort {

    /**
     * Gera o hash de uma senha em texto plano.
     * @param rawPassword A senha a ser criptografada.
     * @return A senha criptografada (hash).
     */
    String hash(String rawPassword);

    /**
     * Verifica se uma senha em texto plano corresponde a um hash.
     * @param rawPassword A senha em texto plano.
     * @param encodedPassword O hash da senha.
     * @return true se as senhas correspondem, false caso contrário.
     */
    boolean check(String rawPassword, String encodedPassword);
}
