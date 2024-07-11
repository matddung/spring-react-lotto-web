FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY src /app/src
RUN chmod +x /app/gradlew
RUN ./gradlew clean build -x test // ./gradlew build
CMD ["java", "-jar", "/app/build/libs/lottoweb-0.0.1-SNAPSHOT.jar"]
