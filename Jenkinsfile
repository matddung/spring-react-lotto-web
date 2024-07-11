pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/matddung/spring-react-lotto-web.git'
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
                sshagent(['d5052907-4fbc-4afb-b6a2-4782f1eccf46']) {
                    sh 'scp -r backend/build/libs/*.jar ubuntu@ec2-43-203-121-200.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/backend'
                    sh 'scp -r frontend/build/* ubuntu@ec2-43-203-121-200.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/frontend'
                    sh 'ssh ubuntu@ec2-43-203-121-200.ap-northeast-2.compute.amazonaws.com "java -jar /home/ubuntu/backend/lottoweb-0.0.1-SNAPSHOT.jar"'
                }
            }
        }
    }
}