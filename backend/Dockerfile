# ---------- 베이스 이미지 ----------
FROM eclipse-temurin:21-jdk

# ---------- 빌드 아규먼트 ----------
ARG JAR_FILE=build/libs/filmus-backend.jar   # 워크플로에서 넘겨줌

# ---------- 애플리케이션 복사 ----------
WORKDIR /app
COPY ${JAR_FILE} app.jar

# ---------- 네트워크 ----------
EXPOSE 8080      # 컨테이너 내부 포트 (Nginx 80 → 8080 프록시)

# ---------- 부트 실행 ----------
ENTRYPOINT ["java","-jar","app.jar"]