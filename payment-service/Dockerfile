FROM docker.io/library/openjdk:17-jdk-alpine
ARG JAR_FILE=target/payment-service-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app.jar"]