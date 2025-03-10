# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim as build

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build the application using Maven directly
RUN mvn clean package

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
