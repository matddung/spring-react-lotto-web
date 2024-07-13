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
                    bat 'copy backend\\src\\main\\resources\\application.default backend\\src\\main\\resources\\application.yml'
                    bat 'powershell -Command "(Get-Content backend\\src\\main\\resources\\application.yml).replace(\'${SPRING_MAIL_USERNAME}\', \'${SPRING_MAIL_USERNAME}\') | Set-Content backend\\src\\main\\resources\\application.yml"'
                    bat 'powershell -Command "(Get-Content backend\\src\\main\\resources\\application.yml).replace(\'${SPRING_MAIL_PASSWORD}\', \'${SPRING_MAIL_PASSWORD}\') | Set-Content backend\\src\\main\\resources\\application.yml"'
                    bat 'powershell -Command "(Get-Content backend\\src\\main\\resources\\application.yml).replace(\'${GOOGLE_CLIENT_ID}\', \'${GOOGLE_CLIENT_ID}\') | Set-Content backend\\src\\main\\resources\\application.yml"'
                    bat 'powershell -Command "(Get-Content backend\\src\\main\\resources\\application.yml).replace(\'${GOOGLE_CLIENT_SECRET}\', \'${GOOGLE_CLIENT_SECRET}\') | Set-Content backend\\src\\main\\resources\\application.yml"'
                    bat 'powershell -Command "(Get-Content backend\\src\\main\\resources\\application.yml).replace(\'${NAVER_CLIENT_ID}\', \'${NAVER_CLIENT_ID}\') | Set-Content backend\\src\\main\\resources\\application.yml"'
                    bat 'powershell -Command "(Get-Content backend\\src\\main\\resources\\application.yml).replace(\'${NAVER_CLIENT_SECRET}\', \'${NAVER_CLIENT_SECRET}\') | Set-Content backend\\src\\main\\resources\\application.yml"'
                    bat 'powershell -Command "(Get-Content backend\\src\\main\\resources\\application.yml).replace(\'${KAKAO_CLIENT_ID}\', \'${KAKAO_CLIENT_ID}\') | Set-Content backend\\src\\main\\resources\\application.yml"'
                    bat 'powershell -Command "(Get-Content backend\\src\\main\\resources\\application.yml).replace(\'${KAKAO_CLIENT_SECRET}\', \'${KAKAO_CLIENT_SECRET}\') | Set-Content backend\\src\\main\\resources\\application.yml"'
                    bat 'powershell -Command "(Get-Content backend\\src\\main\\resources\\application.yml).replace(\'${JWT_SECRET_KEY}\', \'${JWT_SECRET_KEY}\') | Set-Content backend\\src\\main\\resources\\application.yml"'
                }
            }
        }
        stage('Build Backend') {
            steps {
                dir('backend') {
                    script {
                        bat 'gradlew.bat clean build -x test'
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
                    bat 'docker build -t junhyuk1376/backend:latest backend'
                }
            }
        }
        stage('Build Frontend Docker Image') {
            steps {
                script {
                    bat 'docker build -t junhyuk1376/frontend:latest frontend'
                }
            }
        }
        stage('Push Backend Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        bat 'docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD%'
                        bat 'docker push junhyuk1376/backend:latest'
                    }
                }
            }
        }
        stage('Push Frontend Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        bat 'docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD%'
                        bat 'docker push junhyuk1376/frontend:latest'
                    }
                }
            }
        }
        stage('Deploy to AWS') {
            steps {
                script {
                    withCredentials([sshUserPrivateKey(credentialsId: 'my-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                        bat '''
                        echo %SSH_KEY% | openssl base64 -d -A -out id_rsa
                        icacls id_rsa /inheritance:r /grant:r studyjun:F
                        powershell -Command "ssh -i .\\id_rsa -o StrictHostKeyChecking=no ubuntu@ec2-52-78-152-77.ap-northeast-2.compute.amazonaws.com 'cd /home/ubuntu/lottoweb && docker-compose pull && docker-compose up -d'"
                        '''
                    }
                }
            }
        }
    }
}