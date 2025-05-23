pipeline {
    agent any

    environment {
        DB_CONTAINER = 'cicd-mariadb'
        DB_IMAGE = 'mariadb:10.6'
        DB_NAME = 'studyroom'
        DB_PASSWORD = 'root'
        DB_USER = 'root'
        NETWORK_NAME = 'cicd-net'
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
                    for (int i = 0; i < 30; i++) {
                        def logs = sh(script: "docker logs ${DB_CONTAINER} 2>&1 | grep 'ready for connections' || true", returnStdout: true).trim()
                        if (logs) {
                            echo '✅ MariaDB is ready!'
                            ready = true
                            break
                        }
                        sleep 2
                    }
                    if (!ready) {
                        error('❌ MariaDB did not become ready in time.')
                    }
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh """
                        ./mvnw clean package
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

        stage('Push Docker Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker build -t $DOCKER_USER/studyroom-backend ./backend
                        docker build -t $DOCKER_USER/studyroom-frontend ./frontend
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
            """
        }
    }
}
