FROM maven:3.9.11-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Runtime SIN ALPINE
FROM eclipse-temurin:17-jdk
COPY --from=build /target/buscador-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
