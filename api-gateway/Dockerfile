FROM eclipse-temurin:25
ARG JAR_FILE=target/api-gateway-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","/app.jar"]