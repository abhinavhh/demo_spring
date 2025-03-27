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

# Expose port 8080
EXPOSE 8080

# Set environment variables (these are defaults and can be overridden at runtime)
ENV DB_URL=jdbc:postgresql://localhost:5432/postgres
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=Abhinav@123
ENV MAIL_HOST=smtp.gmail.com
ENV MAIL_PORT=587
ENV MAIL_USERNAME=smartirrigation5080@gmail.com
ENV MAIL_PASSWORD="wgxb hxnf wbtp dsds"

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
