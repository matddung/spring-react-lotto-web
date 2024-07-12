FROM jenkins/inbound-agent:latest-jdk11

USER root

RUN curl -fsSL https://deb.nodesource.com/setup_14.x | bash - \
    && apt-get install -y nodejs

USER jenkins