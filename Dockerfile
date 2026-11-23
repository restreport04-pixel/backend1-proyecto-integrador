FROM eclipse-temurin:21-jdk AS build

ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY . $HOME

# Compila el proyecto con Java 21 y activa el modo producción de Vaadin
RUN chmod +x ./mvnw && ./mvnw clean package -Dvaadin.productionMode=true -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Ajusta el uso de memoria para contenedores gratuitos (512 MB) y arranca Spring Boot
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app.jar"]