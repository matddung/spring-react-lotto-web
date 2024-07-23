FROM openjdk:17-jdk-slim

WORKDIR /app

COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle

RUN chmod +x /app/gradlew
RUN ./gradlew --no-daemon clean build -x test || return 0

COPY build/libs/lottoweb-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]