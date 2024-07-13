# First stage: build the application
FROM gradle:8.8-jdk17 AS builder

WORKDIR /app

# Install dos2unix
RUN apt-get update && apt-get install -y dos2unix

# Copy the gradle wrapper and related files first from the backend directory
COPY ./gradlew /app/gradlew
COPY ./gradle /app/gradle
COPY ./build.gradle /app/build.gradle
COPY ./settings.gradle /app/settings.gradle

# Convert gradlew to Unix format
RUN dos2unix /app/gradlew

# Copy the rest of the application source code from the backend directory
COPY ./src /app/src

# Make gradlew executable and build the application
RUN chmod +x /app/gradlew
RUN /app/gradlew --no-daemon clean build -x test

# Second stage: create the runtime image
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy JAR file from the first stage
COPY --from=builder /app/build/libs /app/libs

# Ensure only one JAR file is present and move it
RUN sh -c 'if [ $(ls /app/libs/*.jar | wc -l) = 1 ]; then mv /app/libs/*.jar /app/app.jar; else echo "Error: Multiple JAR files found" && exit 1; fi'

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]