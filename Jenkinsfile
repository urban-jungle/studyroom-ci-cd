pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
    }

    stages {
        stage('Start DB') {
            steps {
                script {
                    sh '''
                        docker rm -f cicd-mariadb || true
                        docker run -d --name cicd-mariadb -e MYSQL_ROOT_PASSWORD=1234 -e MYSQL_DATABASE=studyroom \
                        -p 3306:3306 mariadb:10.6
                        sleep 20
                    '''
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh './mvnw clean package'
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

        stage('Push Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker build -t $DOCKER_USER/studyroom-backend ./backend
                        docker build -t $DOCKER_USER/studyroom-frontend ./frontend
                        docker push $DOCKER_USER/studyroom-backend
                        docker push $DOCKER_USER/studyroom-frontend
                    '''
                }
            }
        }
    }

    post {
        always {
            sh 'docker rm -f cicd-mariadb || true'
        }
    }
}
