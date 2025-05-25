# 1단계: 빌드 이미지
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Maven wrapper를 먼저 복사해서 의존성 캐싱 유도
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline

# 실제 소스 복사 후 빌드
COPY src src
RUN ./mvnw clean package -DskipTests

# 2단계: 실행 이미지
FROM eclipse-temurin:17-jre

WORKDIR /app

# 빌드 결과 jar 복사
COPY --from=builder /app/target/*.jar app.jar

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
