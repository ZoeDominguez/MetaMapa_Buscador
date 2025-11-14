# Etapa de build: Usa Maven 3.9.11 con Eclipse Temurin 17 (LTS)
FROM maven:3.9.11-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa de runtime: Usa Eclipse Temurin 17 JDK Alpine (ligera y mantenida)
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/my-app-name-1.0-SNAPSHOT.jar app.jar

# Render asigna un puerto dinámico; expón 8080 por defecto
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]