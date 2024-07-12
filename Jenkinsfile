pipeline {
    agent any

    environment {
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
                git branch: 'main', url: 'https://github.com/matddung/spring-react-lotto-web.git'
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

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Deploy') {
            steps {
                sshagent(['my-ssh-key']) {
                    sh 'scp -o StrictHostKeyChecking=no -r backend ubuntu@ec2-43-202-59-0.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/'
                    sh 'scp -o StrictHostKeyChecking=no -r frontend ubuntu@ec2-43-202-59-0.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/'
                    sh 'scp -o StrictHostKeyChecking=no docker-compose.yml ubuntu@ec2-43-202-59-0.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/'
                    sh '''
                        ssh -o StrictHostKeyChecking=no ubuntu@ec2-43-202-59-0.ap-northeast-2.compute.amazonaws.com "
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

        stage('Clean Docker Cache') {
            steps {
                sh 'sudo docker system prune -af'
            }
        }

        stage('Clean Gradle Cache') {
            steps {
                dir('backend') {
                    sh './gradlew cleanBuildCache'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}