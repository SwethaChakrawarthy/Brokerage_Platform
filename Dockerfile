# ── Stage 1: Build ────────────────────────────────────────────
# Use Maven + Java 25 to compile and package the app
FROM maven:3.9-eclipse-temurin-25 AS build

# Set working directory inside container
WORKDIR /app

# Copy pom.xml first — Docker caches this layer
# So dependencies are only re-downloaded when pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build JAR file
COPY src ./src
RUN mvn clean package -DskipTests

# ── Stage 2: Run ──────────────────────────────────────────────
# Use lightweight JRE image for running (not full JDK)
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy only the JAR from build stage — keeps image small
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
