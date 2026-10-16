# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# JAVA_OPTS permite ajustar la memoria de la JVM (la define render.yaml).
# "exec" hace que java sea el PID 1 y reciba SIGTERM correctamente.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]