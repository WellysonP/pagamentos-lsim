FROM ubuntu:latest
LABEL authors="wellyson"

# Etapa 1: Usar uma imagem Gradle para construir o JAR
FROM gradle:8.3-jdk17 AS builder

WORKDIR /app

# Copiar os arquivos do projeto
COPY . .

# Rodar o build para gerar o JAR
RUN ./gradlew bootJar

# Etapa 2: Usar uma imagem mais leve para rodar o aplicativo
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Instalar bash (caso não esteja instalado)
RUN apk add --no-cache bash

# Copiar o JAR da etapa de build
COPY --from=builder /app/build/libs/pagamentos-0.0.1-SNAPSHOT.jar /app/pagamentos.jar

# Copiar o script wait-for-it.sh do server-lsim para dentro do gateway-lsim
COPY --from=server-lsim /app/wait-for-it.sh /app/wait-for-it.sh


# Tornar o script executável
RUN chmod +x /app/wait-for-it.sh

# # Definir o comando de execução para aguardar o Eureka Server e então iniciar a aplicação
ENTRYPOINT ["/app/wait-for-it.sh", "server-lsim", "8761", "--", "java", "-jar", "pagamentos.jar"]
