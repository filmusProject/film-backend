# Base image with Java 21
FROM openjdk:21-jdk-slim

# JAR 복사 - JAR 이름이 고정되면 더 안정적
ARG JAR_FILE=build/libs/filmus-backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]