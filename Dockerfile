FROM eclipse-temurin:25-jdk AS build

ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY . $HOME

# Compila el proyecto con el perfil de producción de Vaadin (-Pproduction)
RUN chmod +x ./mvnw && ./mvnw clean package -Pproduction -DskipTests

FROM eclipse-temurin:25-jre-alpine
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Ajusta el uso de memoria para contenedores gratuitos (512 MB) y arranca Spring Boot
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app.jar"]