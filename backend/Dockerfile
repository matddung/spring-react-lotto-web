# First stage: build the application
FROM gradle:8.8-jdk17 AS builder

WORKDIR /app

# Copy the gradle wrapper and related files first from the backend directory
COPY ./gradlew /app/gradlew
COPY ./gradle /app/gradle
COPY ./build.gradle /app/build.gradle
COPY ./settings.gradle /app/settings.gradle

# Copy the rest of the application source code from the backend directory
COPY ./src /app/src

# Make gradlew executable and build the application
RUN chmod +x /app/gradlew
RUN /app/gradlew --no-daemon clean build -x test

# Second stage: create the runtime image
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]