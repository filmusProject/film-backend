# backend/Dockerfile
FROM eclipse-temurin:21-jdk

ARG JAR_FILE=build/libs/filmus-backend.jar

WORKDIR /app
COPY ${JAR_FILE} app.jar

EXPOSE 8080  # 컨테이너 내부 포트 (Nginx 80 → 8080 프록시)

ENTRYPOINT ["java", "-jar", "app.jar"]