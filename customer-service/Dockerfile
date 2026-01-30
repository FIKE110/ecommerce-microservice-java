FROM docker.io/library/openjdk:17-jdk-alpine
ARG JAR_FILE=target/customer-service-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]