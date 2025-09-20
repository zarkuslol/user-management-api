package br.com.sankhya.usermanager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // Sobe o contexto completo da aplicação
@AutoConfigureMockMvc // Configura o MockMvc para fazer requisições HTTP
@Import(TestcontainersConfiguration.class) // Importa a configuração do banco de dados de teste
class HealthCheckIT {

    @Autowired
    private MockMvc mockMvc; // Ferramenta para simular requisições HTTP sem precisar de um servidor real

    @Test
    @DisplayName("Should return status UP when application is running correctly")
    void healthCheck_ShouldReturnUp() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/actuator/health")) // Simula uma requisição GET para o endpoint /actuator/health
                .andExpect(status().isOk()) // Verifica se o status da resposta HTTP é 200 (OK)
                .andExpect(jsonPath("$.status").value("UP")); // Verifica se o corpo da resposta JSON contém o campo "status" com o valor "UP"
    }
}
