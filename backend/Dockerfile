FROM openjdk:17-jdk-slim

WORKDIR /app

ARG SPRING_MAIL_USERNAME
ARG SPRING_MAIL_PASSWORD
ARG GOOGLE_CLIENT_ID
ARG GOOGLE_CLIENT_SECRET
ARG NAVER_CLIENT_ID
ARG NAVER_CLIENT_SECRET
ARG KAKAO_CLIENT_ID
ARG KAKAO_CLIENT_SECRET
ARG JWT_SECRET_KEY

COPY . /app

RUN chmod +x /app/gradlew
RUN ./gradlew --no-daemon clean build -x test

COPY build/libs/lottoweb-0.0.1-SNAPSHOT.jar /app/app.jar

RUN cp src/main/resources/application.default src/main/resources/application.yml && \
    sed -i 's|SPRING_MAIL_USERNAME_PLACEHOLDER|'$SPRING_MAIL_USERNAME'|g' src/main/resources/application.yml && \
    sed -i 's|SPRING_MAIL_PASSWORD_PLACEHOLDER|'$SPRING_MAIL_PASSWORD'|g' src/main/resources/application.yml && \
    sed -i 's|GOOGLE_CLIENT_ID_PLACEHOLDER|'$GOOGLE_CLIENT_ID'|g' src/main/resources/application.yml && \
    sed -i 's|GOOGLE_CLIENT_SECRET_PLACEHOLDER|'$GOOGLE_CLIENT_SECRET'|g' src/main/resources/application.yml && \
    sed -i 's|NAVER_CLIENT_ID_PLACEHOLDER|'$NAVER_CLIENT_ID'|g' src/main/resources/application.yml && \
    sed -i 's|NAVER_CLIENT_SECRET_PLACEHOLDER|'$NAVER_CLIENT_SECRET'|g' src/main/resources/application.yml && \
    sed -i 's|KAKAO_CLIENT_ID_PLACEHOLDER|'$KAKAO_CLIENT_ID'|g' src/main/resources/application.yml && \
    sed -i 's|KAKAO_CLIENT_SECRET_PLACEHOLDER|'$KAKAO_CLIENT_SECRET'|g' src/main/resources/application.yml && \
    sed -i 's|JWT_SECRET_KEY_PLACEHOLDER|'$JWT_SECRET_KEY'|g' src/main/resources/application.yml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]