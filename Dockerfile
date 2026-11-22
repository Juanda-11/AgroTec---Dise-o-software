# Construye la API (Spring Boot) con el frontend incluido como recursos estáticos.
# Contexto de construcción: la raíz del repositorio.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml backend/pom.xml
COPY backend backend
COPY frontend frontend
RUN mvn -q -f backend/pom.xml -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --create-home agrotec
COPY --from=build /app/backend/target/agrotec-backend-1.0.0.jar app.jar
USER agrotec
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
