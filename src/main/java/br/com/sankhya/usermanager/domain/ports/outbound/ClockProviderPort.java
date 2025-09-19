package br.com.sankhya.usermanager.domain.ports.outbound;

import java.time.LocalDateTime;

/**
 * Port para fornecer a data e hora atuais.
 * Abstrai a obtenção do tempo para facilitar os testes.
 */
public interface ClockProviderPort {

    /**
     * @return A data e hora atuais.
     */
    LocalDateTime now();
}
