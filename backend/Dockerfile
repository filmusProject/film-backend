# Base image with Java 21
FROM openjdk:21-jdk-slim

# JAR 복사 - 빌드 결과를 app.jar로 복사
COPY build/libs/filmus-backend.jar app.jar

# 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]