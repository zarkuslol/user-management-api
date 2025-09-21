package br.com.sankhya.usermanager.infrastructure.adapters.outbound.time;

import br.com.sankhya.usermanager.domain.ports.outbound.ClockProviderPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SystemClockProviderAdapter implements ClockProviderPort {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
