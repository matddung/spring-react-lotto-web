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
                    sh 'cp backend/src/main/resources/application.default backend/src/main/resources/application.yml'
                    sh '''
                        sed -i 's|${SPRING_MAIL_USERNAME}|'${SPRING_MAIL_USERNAME}'|' backend/src/main/resources/application.yml
                        sed -i 's|${SPRING_MAIL_PASSWORD}|'${SPRING_MAIL_PASSWORD}'|' backend/src/main/resources/application.yml
                        sed -i 's|${GOOGLE_CLIENT_ID}|'${GOOGLE_CLIENT_ID}'|' backend/src/main/resources/application.yml
                        sed -i 's|${GOOGLE_CLIENT_SECRET}|'${GOOGLE_CLIENT_SECRET}'|' backend/src/main/resources/application.yml
                        sed -i 's|${NAVER_CLIENT_ID}|'${NAVER_CLIENT_ID}'|' backend/src/main/resources/application.yml
                        sed -i 's|${NAVER_CLIENT_SECRET}|'${NAVER_CLIENT_SECRET}'|' backend/src/main/resources/application.yml
                        sed -i 's|${KAKAO_CLIENT_ID}|'${KAKAO_CLIENT_ID}'|' backend/src/main/resources/application.yml
                        sed -i 's|${KAKAO_CLIENT_SECRET}|'${KAKAO_CLIENT_SECRET}'|' backend/src/main/resources/application.yml
                        sed -i 's|${JWT_SECRET_KEY}|'${JWT_SECRET_KEY}'|' backend/src/main/resources/application.yml
                    '''
                }
            }
        }
        stage('Build Backend') {
            steps {
                dir('backend') {
                    script {
                        bat './gradlew clean build -x test'
                    }
                }
            }
        }
        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    script {
                        bat 'npm install'
                        bat 'npm run build'
                    }
                }
            }
        }
        stage('Build Backend Docker Image') {
            steps {
                script {
                    docker.build('junhyuk1376/backend:latest', 'backend')
                }
            }
        }
        stage('Build Frontend Docker Image') {
            steps {
                script {
                    docker.build('junhyuk1376/frontend:latest', 'frontend')
                }
            }
        }
        stage('Remove Unused Files') {
            steps {
                script {
                    bat '''
                    docker run --name temp-backend-container junhyuk1376/backend:latest powershell -Command "
                    Remove-Item -Recurse -Force /app/Dockerfile;
                    Remove-Item -Recurse -Force /app/Jenkinsfile;
                    Remove-Item -Recurse -Force /app/docker-compose.yml;
                    Remove-Item -Recurse -Force /app/.dockerignore;
                    Remove-Item -Recurse -Force /app/.gitignore;
                    "
                    docker commit temp-backend-container junhyuk1376/backend:latest
                    docker rm temp-backend-container

                    docker run --name temp-frontend-container junhyuk1376/frontend:latest powershell -Command "
                    Remove-Item -Recurse -Force /app/Dockerfile;
                    Remove-Item -Recurse -Force /app/Jenkinsfile;
                    Remove-Item -Recurse -Force /app/docker-compose.yml;
                    Remove-Item -Recurse -Force /app/.dockerignore;
                    Remove-Item -Recurse -Force /app/.gitignore;
                    "
                    docker commit temp-frontend-container junhyuk1376/frontend:latest
                    docker rm temp-frontend-container
                    '''
                }
            }
        }
        stage('Push Backend Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKERHUB_CREDENTIALS) {
                        docker.image('junhyuk1376/backend:latest').push('latest')
                    }
                }
            }
        }
        stage('Push Frontend Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKERHUB_CREDENTIALS) {
                        docker.image('junhyuk1376/frontend:latest').push('latest')
                    }
                }
            }
        }
        stage('Deploy to AWS') {
            steps {
                script {
                    bat '''
                    ssh -o StrictHostKeyChecking=no ubuntu@ec2-52-78-152-77.ap-northeast-2.compute.amazonaws.com "
                    cd /home/ubuntu/lottoweb
                    docker-compose pull
                    docker-compose up -d
                    "
                    '''
                }
            }
        }
    }
}