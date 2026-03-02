# Use a lightweight JDK to run the application
FROM eclipse-temurin:25
WORKDIR /app

# Copy the pre-built jar from your host machine
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 9090

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
