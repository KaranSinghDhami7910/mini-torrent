# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the target directory to the container
COPY target/*.jar app.jar

# Expose the application port (change it if needed)
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
