FROM gradle:8.8-jdk17 AS builder

WORKDIR /app
COPY . /app
RUN chmod +x gradlew
RUN ./gradlew --no-daemon clean build -x test


FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]