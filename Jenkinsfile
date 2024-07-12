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
                    sh './gradlew clean build'
                }
            }
        }

        stage('Test Backend') {
            steps {
                dir('backend') {
                    sh './gradlew test'
                }
            }
        }

        stage('Deploy') {
            steps {
                sshagent(['my-ssh-key']) {
                    sh 'scp -r backend/build/libs/*.jar ubuntu@ec2-3-38-102-129.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/backend'
                    sh 'scp -r frontend/build/* ubuntu@ec2-3-38-102-129.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/frontend'
                    sh 'ssh ubuntu@ec2-3-38-102-129.ap-northeast-2.compute.amazonaws.com "java -jar /home/ubuntu/backend/lottoweb-0.0.1-SNAPSHOT.jar"'
                }
            }
        }
    }
}
