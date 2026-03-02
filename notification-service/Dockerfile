FROM eclipse-temurin:25
ARG JAR_FILE=target/notification-service-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","/app.jar"]