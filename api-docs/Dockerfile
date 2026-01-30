FROM docker.io/library/openjdk:17-jdk-alpine
ARG JAR_FILE=target/api-docs-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9091
ENTRYPOINT ["java","-jar","/app.jar"]