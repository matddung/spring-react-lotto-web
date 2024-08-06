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
                        sed -i 's|SPRING_MAIL_USERNAME_PLACEHOLDER|'${SPRING_MAIL_USERNAME}'|' backend/src/main/resources/application.yml
                        sed -i 's|SPRING_MAIL_PASSWORD_PLACEHOLDER|'${SPRING_MAIL_PASSWORD}'|' backend/src/main/resources/application.yml
                        sed -i 's|GOOGLE_CLIENT_ID_PLACEHOLDER|'${GOOGLE_CLIENT_ID}'|' backend/src/main/resources/application.yml
                        sed -i 's|GOOGLE_CLIENT_SECRET_PLACEHOLDER|'${GOOGLE_CLIENT_SECRET}'|' backend/src/main/resources/application.yml
                        sed -i 's|NAVER_CLIENT_ID_PLACEHOLDER|'${NAVER_CLIENT_ID}'|' backend/src/main/resources/application.yml
                        sed -i 's|NAVER_CLIENT_SECRET_PLACEHOLDER|'${NAVER_CLIENT_SECRET}'|' backend/src/main/resources/application.yml
                        sed -i 's|KAKAO_CLIENT_ID_PLACEHOLDER|'${KAKAO_CLIENT_ID}'|' backend/src/main/resources/application.yml
                        sed -i 's|KAKAO_CLIENT_SECRET_PLACEHOLDER|'${KAKAO_CLIENT_SECRET}'|' backend/src/main/resources/application.yml
                        sed -i 's|JWT_SECRET_KEY_PLACEHOLDER|'${JWT_SECRET_KEY}'|' backend/src/main/resources/application.yml
                    '''
                }
            }
        }
        stage('Prepare Alertmanager Configuration') {
            steps {
                script {
                    sh '''
                        sed -i 's|SPRING_MAIL_USERNAME_PLACEHOLDER|'${SPRING_MAIL_USERNAME}'|' alertmanager.yml
                        sed -i 's|SPRING_MAIL_PASSWORD_PLACEHOLDER|'${SPRING_MAIL_PASSWORD}'|' alertmanager.yml
                    '''
                }
            }
        }
        stage('Deploy to AWS') {
            steps {
                script {
                    sshagent(['my-ssh-key']) {
                        sh 'rsync -avz -e "ssh -o StrictHostKeyChecking=no" docker-compose.yml ubuntu@ec2-43-203-173-180.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/'
                        sh 'rsync -avz -e "ssh -o StrictHostKeyChecking=no" prometheus.yml ubuntu@ec2-43-203-173-180.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/'
                        sh 'rsync -avz -e "ssh -o StrictHostKeyChecking=no" alertmanager.yml ubuntu@ec2-43-203-173-180.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/'
                        sh 'rsync -avz -e "ssh -o StrictHostKeyChecking=no" alert.rules.yml ubuntu@ec2-43-203-173-180.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/'
                    }
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
                        ssh -o StrictHostKeyChecking=no ubuntu@ec2-43-203-173-180.ap-northeast-2.compute.amazonaws.com "
                            sudo docker-compose down &&
                            sudo docker system prune -a -f
                        "
                    '''
                }
            }
        }
        stage('Build Backend Docker Image') {
            steps {
                dir('backend') {
                    script {
                        sh '''
                            docker build --build-arg SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME} \
                                         --build-arg SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD} \
                                         --build-arg GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} \
                                         --build-arg GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} \
                                         --build-arg NAVER_CLIENT_ID=${NAVER_CLIENT_ID} \
                                         --build-arg NAVER_CLIENT_SECRET=${NAVER_CLIENT_SECRET} \
                                         --build-arg KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID} \
                                         --build-arg KAKAO_CLIENT_SECRET=${KAKAO_CLIENT_SECRET} \
                                         --build-arg JWT_SECRET_KEY=${JWT_SECRET_KEY} \
                                         -t junhyuk1376/backend:latest .
                        '''
                    }
                }
            }
        }
        stage('Build Frontend Docker Image') {
            steps {
                dir('frontend') {
                    script {
                        sh 'docker build -t junhyuk1376/frontend:latest .'
                    }
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
        stage('Run Docker Compose') {
            steps {
                sshagent(['my-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ubuntu@ec2-43-203-173-180.ap-northeast-2.compute.amazonaws.com "
                            cd /home/ubuntu &&
                            export SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME} &&
                            export SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD} &&
                            export GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} &&
                            export GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} &&
                            export NAVER_CLIENT_ID=${NAVER_CLIENT_ID} &&
                            export NAVER_CLIENT_SECRET=${NAVER_CLIENT_SECRET} &&
                            export KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID} &&
                            export KAKAO_CLIENT_SECRET=${KAKAO_CLIENT_SECRET} &&
                            export JWT_SECRET_KEY=${JWT_SECRET_KEY} &&
                            sudo SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME} \
                                SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD} \
                                GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} \
                                GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} \
                                NAVER_CLIENT_ID=${NAVER_CLIENT_ID} \
                                NAVER_CLIENT_SECRET=${NAVER_CLIENT_SECRET} \
                                KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID} \
                                KAKAO_CLIENT_SECRET=${KAKAO_CLIENT_SECRET} \
                                JWT_SECRET_KEY=${JWT_SECRET_KEY} \
                                docker-compose up -d --build
                        "
                    '''
                }
            }
        }
    }
}