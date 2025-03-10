# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim as build

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Give permission to Maven Wrapper
RUN chmod +x ./mvnw

# Build the application
RUN ./mvnw clean package

# ===================
# RUN PHASE (Second Stage)
# ===================
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port (usually 8080)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
