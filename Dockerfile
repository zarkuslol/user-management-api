# Etapa 1: Build da aplicação
FROM gradle:8.4-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Etapa 2: Imagem final
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o JAR gerado
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
