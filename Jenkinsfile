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
                    sh '''
                    docker run --name temp-backend-container junhyuk1376/backend:latest /bin/sh -c "
                    rm -rf /app/Dockerfile \
                           /app/Jenkinsfile \
                           /app/docker-compose.yml \
                           /app/.dockerignore \
                           /app/.gitignore"
                    docker commit temp-backend-container junhyuk1376/backend:latest
                    docker rm temp-backend-container

                    docker run --name temp-frontend-container junhyuk1376/frontend:latest /bin/sh -c "
                    rm -rf /app/Dockerfile \
                           /app/Jenkinsfile \
                           /app/docker-compose.yml \
                           /app/.dockerignore \
                           /app/.gitignore"
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
                    sh '''
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