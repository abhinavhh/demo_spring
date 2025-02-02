# Use an official OpenJDK image as the base image
FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the project files to the container
COPY . .

# Grant execute permissions to the Maven wrapper
RUN chmod +x ./mvnw

# Build the application using Maven
RUN ./mvnw clean package

# Expose the port the application will run on
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "target/spring_boot.jar"]
