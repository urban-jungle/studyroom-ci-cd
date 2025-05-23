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
              echo '⏳ Waiting for MariaDB to become available...'
              def ready = false
              for (int i = 0; i < 20; i++) {
                def output = sh(script: "docker logs cicd-mariadb 2>&1 | grep 'ready for connections' || true", returnStdout: true).trim()
                if (output) {
                  echo '✅ MariaDB is ready!'
                  ready = true
                  break
                }
                echo "Waiting for MariaDB... (${i + 1}/20)"
                sleep 3
              }
              if (!ready) {
                echo '❌ MariaDB is not responding in time.'
                sh 'docker logs cicd-mariadb'
                error("MariaDB did not become ready.")
              }
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
