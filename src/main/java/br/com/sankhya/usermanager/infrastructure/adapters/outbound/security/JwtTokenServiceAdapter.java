package br.com.sankhya.usermanager.infrastructure.adapters.outbound.security;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.outbound.ClockProviderPort;
import br.com.sankhya.usermanager.domain.ports.outbound.TokenServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import io.jsonwebtoken.Clock;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtTokenServiceAdapter implements TokenServicePort {

    private final String jwtSecret;
    private final long jwtExpirationMs;
    private final ClockProviderPort clockProvider;

    // Injeção via construtor para todas as dependências
    public JwtTokenServiceAdapter(
            @Value("${app.security.jwt.secret}") String jwtSecret,
            @Value("${app.security.jwt.expiration-ms}") long jwtExpirationMs,
            ClockProviderPort clockProvider) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
        this.clockProvider = clockProvider;
    }

    @Override
    public String generateToken(User user) {
        LocalDateTime now = clockProvider.now();
        Date issueDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date expiryDate = new Date(issueDate.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(issueDate)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        // Cria um 'Clock' do JWT a partir do nosso ClockProviderPort
        final Clock jwtClock = () -> Date.from(clockProvider.now().atZone(ZoneId.systemDefault()).toInstant());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setClock(jwtClock) // <-- A CORREÇÃO ESTÁ AQUI!
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
