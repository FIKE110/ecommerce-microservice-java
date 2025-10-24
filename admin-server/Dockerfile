# Use a lightweight JDK to run the application
FROM docker.io/library/openjdk:17-jdk-alpine
WORKDIR /app

# Copy the pre-built jar from your host machine
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 9090

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
