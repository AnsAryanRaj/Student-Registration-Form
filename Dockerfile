# ==========================================
# Multi-stage Dockerfile for Student Registration Form
# ==========================================

# Stage 1: Build the Maven application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .

# Copy source code and build package
COPY src ./src
RUN mvn clean package -DskipTests=false

# Stage 2: Lightweight runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/student-registration-form-1.0.0.jar app.jar

# Expose application port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
