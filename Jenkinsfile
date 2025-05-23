pipeline {
    agent any

    environment {
        DB_CONTAINER = 'cicd-mariadb'
        DB_PORT = '13306'
        MYSQL_ROOT_PASSWORD = '1234'
        MYSQL_DATABASE = 'studyroom'
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
    }

    stages {
        stage('Start MariaDB') {
            steps {
                script {
                    sh """
                        docker rm -f $DB_CONTAINER || true
                        docker run -d --name $DB_CONTAINER \\
                          -e MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD \\
                          -e MYSQL_DATABASE=$MYSQL_DATABASE \\
                          -p $DB_PORT:3306 mariadb:10.6
                    """
                }
            }
        }

        stage('Wait for MariaDB Ready') {
            steps {
                script {
                    sh '''
                        echo "⏳ Checking MariaDB availability..."
                        for i in {1..10}; do
                            if docker exec cicd-mariadb mysqladmin ping -uroot -p1234 --silent; then
                                echo "✅ MariaDB is ready!"
                                exit 0
                            fi
                            echo "Waiting for MariaDB to be ready... ($i/10)"
                            sleep 3
                        done
                        echo "❌ MariaDB did not start in time"
                        docker logs cicd-mariadb || true
                        exit 1
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
            sh "docker rm -f $DB_CONTAINER || true"
        }
    }
}
