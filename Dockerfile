# Use Maven image to build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the project files
COPY . .

# Build the application without running tests
RUN mvn clean package -DskipTests

# Use JDK to run the application
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy the built jar file from the previous step
COPY --from=build /app/target/*.jar app.jar

# Expose port (optional)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
