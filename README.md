# User Management API

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen)
![Docker](https://img.shields.io/badge/Docker-20.10-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Gradle](https://img.shields.io/badge/Gradle-8.4-green)

API REST para gerenciamento de usuários desenvolvida como um case técnico, demonstrando conceitos de Arquitetura Hexagonal, segurança com JWT, testes automatizados e containerização com Docker.

## ✨ Features

* **Autenticação Segura**: Sistema de login com JWT (JSON Web Token).
* **CRUD de Usuários**: Operações completas de Criação, Leitura, Atualização e Deleção de usuários.
* **Paginação**: Listagem de usuários com suporte a paginação.
* **Arquitetura Hexagonal**: Lógica de negócio isolada da infraestrutura, seguindo os princípios de *Ports and Adapters*.
* **Containerização**: Aplicação e banco de dados totalmente gerenciados por Docker e Docker Compose.
* **Testes Automatizados**: Cobertura de testes unitários e de integração (incluindo Testcontainers).
* **Documentação Interativa**: Endpoints documentados com Swagger (OpenAPI 3).

## 🛠️ Tech Stack

* **Linguagem**: Java 21
* **Framework**: Spring Boot 3.3.4
    * Spring Web
    * Spring Data JPA
    * Spring Security
* **Banco de Dados**: PostgreSQL 16
* **Build Tool**: Gradle
* **Containerização**: Docker / Docker Compose
* **Documentação**: Springdoc OpenAPI (Swagger UI)
* **Testes**: JUnit 5, Mockito, Testcontainers

## 🚀 Getting Started

Siga as instruções abaixo para configurar e executar o projeto em seu ambiente local.

### Pré-requisitos

* [Git](https://git-scm.com/)
* [Java JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
* [Docker](https://www.docker.com/get-started/)
* [Docker Compose](https://docs.docker.com/compose/install/)

### ⚙️ Instalação e Execução

1.  **Clone o repositório:**
    ```bash
    git clone <URL_DO_SEU_REPOSITORIO>
    cd user-management-api
    ```

2.  **Crie o arquivo de variáveis de ambiente:**
    Crie um arquivo chamado `.env` na raiz do projeto. Ele é essencial para configurar o banco de dados e as chaves de segurança.

3.  **Copie e cole o seguinte conteúdo no seu arquivo `.env`:**
    ```env
    # ========== CONFIGURAÇÕES DO BANCO DE DADOS ==========
    DB_NAME=usersdb
    DB_USER=prod_user
    DB_PASSWORD=prod_pass

    # ========== CONFIGURAÇÕES DE SEGURANÇA (JWT) ==========
    APP_SECURITY_JWT_SECRET=MySuperSecretKeyForTestingPurposesThatIsLongEnough
    APP_SECURITY_JWT_EXPIRATION_TIME=3600000

    # ========== USUÁRIO ADMINISTRADOR PADRÃO ==========
    ADMIN_USER=admin
    ADMIN_PASSWORD=admin123
    ```

4.  **Construa e inicie os contêineres Docker:**
    Este comando irá construir a imagem da aplicação Java e iniciar os serviços da API e do banco de dados.
    ```bash
    docker-compose up --build
    ```
    Para rodar em background (modo detached), use `docker-compose up --build -d`.

5.  **Verifique os logs (opcional):**
    Para acompanhar a inicialização da aplicação e consultar os logs em tempo real, use o comando:
    ```bash
    docker-compose logs -f api
    ```
    A API estará pronta para uso quando você vir uma mensagem similar a `Started UsermanagerApplication in X.XXX seconds`.

## 🧪 Rodando os Testes

Para garantir que tudo está funcionando corretamente, você pode rodar a suíte completa de testes automatizados. O comando abaixo irá limpar o projeto, compilar o código e executar todos os testes unitários e de integração.

```bash
./gradlew clean build
```

## 📖 Documentação da API (Swagger)
Com a aplicação em execução, a documentação interativa da API estará disponível no seu navegador. Através dela, é possível visualizar todos os endpoints, seus DTOs e testá-los diretamente.

- URL da Documentação: http://localhost:8080/swagger-ui/index.html

### Autenticação no Swagger

Para testar os endpoints protegidos:

1. Use o endpoint POST /auth/login para obter um token JWT.

2. Clique no botão "Authorize" no canto superior direito da página.

3. Na janela que abrir, cole o token JWT e clique em "Authorize".

4. Agora você pode executar qualquer endpoint protegido diretamente pela interface.