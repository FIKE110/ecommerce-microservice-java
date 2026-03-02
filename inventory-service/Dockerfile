FROM eclipse-temurin:25
ARG JAR_FILE=target/inventory-service-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app.jar"]