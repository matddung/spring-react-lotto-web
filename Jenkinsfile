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
        stage('Deploy to AWS') {
            steps {
                script {
                    sshagent(['my-ssh-key']) {
                        sh 'rsync -avz -e "ssh -o StrictHostKeyChecking=no" docker-compose.yml ubuntu@ec2-3-39-253-54.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/'
                    }
                }
            }
        }
        stage('Run Docker Compose') {
            steps {
                sshagent(['my-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ubuntu@ec2-3-39-253-54.ap-northeast-2.compute.amazonaws.com "
                            cd /home/ubuntu &&
                            sudo docker-compose up -d --build
                        "
                    '''
                }
            }
        }
    }
}