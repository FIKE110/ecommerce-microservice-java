FROM eclipse-temurin:25
ARG JAR_FILE=target/config-server-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","/app.jar"]