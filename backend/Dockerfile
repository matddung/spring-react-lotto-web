FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y unzip

ADD https://services.gradle.org/distributions/gradle-8.8-bin.zip /gradle-8.8-bin.zip
RUN mkdir /gradle && unzip /gradle-8.8-bin.zip -d /gradle

ENV GRADLE_HOME /gradle/gradle-8.8
ENV PATH ${GRADLE_HOME}/bin:${PATH}

WORKDIR /app
COPY . /app
RUN chmod +x /app/gradlew
RUN ./gradlew --no-daemon clean build -x test
COPY ./build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]