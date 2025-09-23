package br.com.sankhya.usermanager.infrastructure.adapters.inbound.security;

import br.com.sankhya.usermanager.domain.ports.outbound.TokenServicePort;
import br.com.sankhya.usermanager.domain.ports.outbound.UserRepositoryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenServicePort tokenServicePort;
    private final UserRepositoryPort userRepositoryPort;

    public JwtAuthenticationFilter(TokenServicePort tokenServicePort, UserRepositoryPort userRepositoryPort) {
        this.tokenServicePort = tokenServicePort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (token != null) {
            String username = tokenServicePort.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Aqui, estamos usando nosso User do domínio como UserDetails.
                userRepositoryPort.findByUsername(username).ifPresent(user -> {
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password(user.getPassword().hash()) // A senha não é usada para validação aqui, mas é parte do contrato
                            .build();

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
