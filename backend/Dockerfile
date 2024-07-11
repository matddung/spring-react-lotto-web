FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build.gradle /app

COPY src /app/src

RUN ./gradlew build

CMD ["java", "-jar", "/app/build/libs/lottoweb-0.0.1-SNAPSHOT.jar"]