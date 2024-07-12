pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
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
                    sh '''
                    ssh -o StrictHostKeyChecking=no ubuntu@ec2-52-78-152-77.ap-northeast-2.compute.amazonaws.com <<EOF
                    cd /path/to/your/deployment
                    docker-compose pull
                    docker-compose up -d
                    EOF
                    '''
                }
            }
        }
    }
}