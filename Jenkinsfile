pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        SPRING_MAIL_USERNAME = credentials('spring.mail.username')
        SPRING_MAIL_PASSWORD = credentials('spring.mail.password')
        GOOGLE_CLIENT_ID = credentials('google.client-id')
        GOOGLE_CLIENT_SECRET = credentials('google.client-secret')
        NAVER_CLIENT_ID = credentials('naver.client-id')
        NAVER_CLIENT_SECRET = credentials('naver.client-secret')
        KAKAO_CLIENT_ID = credentials('kakao.client-id')
        KAKAO_CLIENT_SECRET = credentials('kakao.client-secret')
        JWT_SECRET_KEY = credentials('jwt.secret-key')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scmGit(branches: [[name: 'main']],
                                userRemoteConfigs: [[url: 'https://github.com/matddung/spring-react-lotto-web']])
            }
        }
        stage('Prepare Backend Configuration') {
            steps {
                script {
                    sh 'wsl cp backend/src/main/resources/application.default backend/src/main/resources/application.yml'
                    sh '''
                        wsl sed -i 's|${SPRING_MAIL_USERNAME}|'${SPRING_MAIL_USERNAME}'|' backend/src/main/resources/application.yml
                        wsl sed -i 's|${SPRING_MAIL_PASSWORD}|'${SPRING_MAIL_PASSWORD}'|' backend/src/main/resources/application.yml
                        wsl sed -i 's|${GOOGLE_CLIENT_ID}|'${GOOGLE_CLIENT_ID}'|' backend/src/main/resources/application.yml
                        wsl sed -i 's|${GOOGLE_CLIENT_SECRET}|'${GOOGLE_CLIENT_SECRET}'|' backend/src/main/resources/application.yml
                        wsl sed -i 's|${NAVER_CLIENT_ID}|'${NAVER_CLIENT_ID}'|' backend/src/main/resources/application.yml
                        wsl sed -i 's|${NAVER_CLIENT_SECRET}|'${NAVER_CLIENT_SECRET}'|' backend/src/main/resources/application.yml
                        wsl sed -i 's|${KAKAO_CLIENT_ID}|'${KAKAO_CLIENT_ID}'|' backend/src/main/resources/application.yml
                        wsl sed -i 's|${KAKAO_CLIENT_SECRET}|'${KAKAO_CLIENT_SECRET}'|' backend/src/main/resources/application.yml
                        wsl sed -i 's|${JWT_SECRET_KEY}|'${JWT_SECRET_KEY}'|' backend/src/main/resources/application.yml
                    '''
                }
            }
        }
        stage('Build Backend') {
            steps {
                dir('backend') {
                    script {
                        sh 'wsl ./gradlew clean build -x test'
                    }
                }
            }
        }
        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    script {
                        sh 'wsl npm install'
                        sh 'wsl npm run build'
                    }
                }
            }
        }
        stage('Build Backend Docker Image') {
            steps {
                script {
                    sh 'wsl docker build -t junhyuk1376/backend:latest backend'
                }
            }
        }
        stage('Build Frontend Docker Image') {
            steps {
                script {
                    sh 'wsl docker build -t junhyuk1376/frontend:latest frontend'
                }
            }
        }
        stage('Push Backend Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "echo $DOCKER_PASSWORD | wsl docker login -u $DOCKER_USERNAME --password-stdin"
                        sh "wsl docker push junhyuk1376/backend:latest"
                    }
                }
            }
        }
        stage('Push Frontend Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "echo $DOCKER_PASSWORD | wsl docker login -u $DOCKER_USERNAME --password-stdin"
                        sh "wsl docker push junhyuk1376/frontend:latest"
                    }
                }
            }
        }
        stage('Deploy to AWS') {
            steps {
                sshagent(['my-ssh-key']) {
                    sh '''
                    wsl echo "$SSH_KEY" > ~/.ssh/id_rsa
                    wsl chmod 600 ~/.ssh/id_rsa
                    wsl ssh -i ~/.ssh/id_rsa -o StrictHostKeyChecking=no ubuntu@ec2-52-78-152-77.ap-northeast-2.compute.amazonaws.com "cd /home/ubuntu/lottoweb && docker-compose pull && docker-compose up -d"
                    '''
                }
            }
        }
    }
}