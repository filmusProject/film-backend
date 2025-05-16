# Dockerfile (위치: film-backend/backend/Dockerfile)
FROM eclipse-temurin:21-jdk

ARG JAR_FILE=build/libs/filmus-backend.jar

WORKDIR /app
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]