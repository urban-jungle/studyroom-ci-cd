pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
        DOCKERHUB_USERNAME = 'jungmin2'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh './mvnw clean package'
                    sh "docker build -t $DOCKERHUB_USERNAME/studyroom-backend ."
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh "docker build -t $DOCKERHUB_USERNAME/studyroom-frontend ."
                }
            }
        }

        stage('Push Images') {
            steps {
                sh "echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_USERNAME --password-stdin"
                sh "docker push $DOCKERHUB_USERNAME/studyroom-backend"
                sh "docker push $DOCKERHUB_USERNAME/studyroom-frontend"
            }
        }
    }
}
