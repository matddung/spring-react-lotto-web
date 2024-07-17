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
                        sh 'chmod +x gradlew'
                        sh './gradlew clean build -x test'
                    }
                }
            }
        }
        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    script {
                        sh 'npm install'
                        sh 'npm run build'
                    }
                }
            }
        }
        stage('Clean Up Docker Containers and Volumes') {
            steps {
                sshagent(['my-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ubuntu@sbsmily.shop "
                            sudo docker-compose down -v
                            sudo docker system prune -a -f
                        "
                    '''
                }
            }
        }
        stage('Build Backend Docker Image') {
            steps {
                script {
                    sh 'docker build -t junhyuk1376/backend:latest backend'
                }
            }
        }
        stage('Build Frontend Docker Image') {
            steps {
                script {
                    sh 'docker build -t junhyuk1376/frontend:latest frontend'
                }
            }
        }
        stage('Push Backend Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"
                        sh "docker push junhyuk1376/backend:latest"
                    }
                }
            }
        }
        stage('Push Frontend Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"
                        sh "docker push junhyuk1376/frontend:latest"
                    }
                }
            }
        }
        stage('Deploy to AWS') {
            steps {
                sshagent(['my-ssh-key']) {
                    sh 'scp -o StrictHostKeyChecking=no -r backend ubuntu@sbsmily.shop:/home/ubuntu/'
                    sh 'scp -o StrictHostKeyChecking=no -r frontend ubuntu@sbsmily.shop:/home/ubuntu/'
                    sh 'scp -o StrictHostKeyChecking=no docker-compose.yml ubuntu@sbsmily.shop:/home/ubuntu/'
                    sh '''
                        ssh -o StrictHostKeyChecking=no ubuntu@sbsmily.shop "
                            sudo chown -R ubuntu:ubuntu /home/ubuntu/backend /home/ubuntu/frontend /home/ubuntu/docker-compose.yml &&
                            cd /home/ubuntu &&
                            ls -al /home/ubuntu &&
                            ls -al /home/ubuntu/backend &&
                            sudo docker-compose up -d --build
                        "
                    '''
                }
            }
        }
    }
}