pipeline {
    agent any

    environment {
        DB_CONTAINER = 'cicd-mariadb'
        DB_IMAGE = 'mariadb:10.6'
        DB_NAME = 'studyroom'
        DB_PASSWORD = 'root'
        DB_USER = 'root'
        NETWORK_NAME = 'cicd-net'
        BACKEND_CONTAINER = 'studyroom-backend'
        FRONTEND_CONTAINER = 'studyroom-frontend'
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
    }

    stages {
        stage('Start MariaDB') {
            steps {
                script {
                    sh """
                        docker network create ${NETWORK_NAME} || true
                        docker rm -f ${DB_CONTAINER} || true
                        docker run -d --name ${DB_CONTAINER} \\
                            --network ${NETWORK_NAME} \\
                            -e MYSQL_ROOT_PASSWORD=${DB_PASSWORD} \\
                            -e MYSQL_DATABASE=${DB_NAME} \\
                            ${DB_IMAGE}
                    """
                }
            }
        }

        stage('Wait for MariaDB Ready') {
            steps {
                script {
                    echo '⏳ Waiting for MariaDB to be ready...'
                    def ready = false
                    for (int i = 0; i < 10; i++) {
                        def result = sh(
                            script: "docker exec ${DB_CONTAINER} mysqladmin ping -h localhost -u${DB_USER} -p${DB_PASSWORD} --silent",
                            returnStatus: true
                        )
                        if (result == 0) {
                            echo '✅ MariaDB is ready!'
                            ready = true
                            break
                        }
                        echo "❗ MariaDB not ready. Retrying in 5s..."
                        sleep 5
                    }
                    if (!ready) {
                        error('❌ MariaDB did not become ready in time.')
                    }
                }
            }
        }

        stage('Build Backend JAR') {
            steps {
                dir('backend') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                script {
                    sh 'docker build -t studyroom-backend ./backend'
                }
            }
        }

        stage('Run Backend Container') {
            steps {
                script {
                    sh """
                        docker rm -f studyroom-backend || true
                        docker run -d --name studyroom-backend --network ${NETWORK_NAME} -p 8089:8081 studyroom-backend
                    """
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh """
                        npm install
                        npm run build
                    """
                }
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                script {
                    sh 'docker build -t studyroom-frontend ./frontend'
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker tag studyroom-backend $DOCKER_USER/studyroom-backend
                        docker tag studyroom-frontend $DOCKER_USER/studyroom-frontend
                        docker push $DOCKER_USER/studyroom-backend
                        docker push $DOCKER_USER/studyroom-frontend
                    """
                }
            }
        }
    }

    post {
        always {
            sh """
                docker rm -f ${DB_CONTAINER} || true
                docker rm -f ${BACKEND_CONTAINER} || true
            """
        }
    }
}
