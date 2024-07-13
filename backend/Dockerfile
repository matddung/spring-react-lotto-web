FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . /app
RUN chmod +x /app/gradlew
RUN ./gradlew --no-daemon clean build -x test
COPY build/libs/lottoweb-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]