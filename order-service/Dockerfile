FROM docker.io/library/openjdk:17-jdk-alpine
ARG JAR_FILE=target/order-service-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app.jar"]