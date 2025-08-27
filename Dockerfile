FROM ubuntu:latest
LABEL authors="wellyson"

FROM gradle:8.3-jdk17 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew bootJar

FROM eclipse-temurin

WORKDIR /app

RUN apt-get update && apt-get install -y bash && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/pagamentos-0.0.1-SNAPSHOT.jar /app/pagamentos.jar

ENTRYPOINT ["java", "-jar", "pagamentos.jar"]
