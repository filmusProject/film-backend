# Java 21이 설치된 공식 이미지 기반
FROM openjdk:21-jdk-slim

# JAR 파일을 컨테이너에 복사
COPY build/libs/*.jar app.jar

# 앱 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
