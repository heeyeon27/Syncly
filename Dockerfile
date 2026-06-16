# ── Stage 1: Build ───────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Gradle Wrapper + 빌드 설정 파일만 먼저 복사 (의존성 레이어 캐시)
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew

# 의존성 미리 다운로드 (소스 변경 시 이 레이어는 재사용됨)
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 후 JAR 빌드 (테스트 스킵)
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 보안: 비루트 사용자로 실행
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=builder /app/build/libs/*.jar app.jar

# Render는 $PORT 환경변수로 포트를 주입함 (기본 8080)
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java -jar app.jar --server.port=${PORT:-8080}"]
