pipeline {
    agent none

    environment {
        APP_NAME = "training-portal"
        AWS_REGION = "us-east-1"
        ECR_REGISTRY = "110425445190.dkr.ecr.us-east-1.amazonaws.com"
    }

    tools {
        maven 'Maven'
    }

    stages {
        stage("checkout") {
            agent {
                label 'built-in'
            }
            steps {
                deleteDir()
                checkout scm

                stash name: 'source-code', includes: '**'
            }
        }

        stage("get git commit") {
            agent {
                label 'built-in'
            }
            steps {
                script {
                    env.IMAGE_TAG = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                }
            }
        }

        stage("build") {
            agent {
                label 'built-in'
            }
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage("archive artifact") {
            agent {
                label 'built-in'
            }
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage("build docker image") {
            agent {
                label 'built-in'
            }
            steps {
                sh """
                    docker build -t training-portal:${IMAGE_TAG} \
                    -t training-portal:latest .
                """
            }
        }

        stage("login to ECR") {
            agent {
                label 'built-in'
            }
            steps {
                sh """
                    aws ecr get-login-password --region ${AWS_REGION} | docker login \
                    --username AWS \
                    --password-stdin ${ECR_REGISTRY}
                """
            }
        }

        stage("Tag docker image") {
            agent {
                label 'built-in'
            }
            steps {
                sh """
                    docker tag ${APP_NAME}:${IMAGE_TAG} \
                    ${ECR_REGISTRY}/${APP_NAME}:${IMAGE_TAG}

                    docker tag ${APP_NAME}:latest \
                    ${ECR_REGISTRY}/${APP_NAME}:latest
                """
            }
        }

        stage("Push Image to Amazon ECR") {
            agent {
                label 'built-in'
            }
            steps {
                sh """
                    docker push \
                    ${ECR_REGISTRY}/${APP_NAME}:${IMAGE_TAG}

                    docker push \
                    ${ECR_REGISTRY}/${APP_NAME}:latest
                """
            }
        }

        stage('Deploy') {
            agent {
                label 'app-server'
            }

            steps {
                unstash 'source-code'

                sh '''
                    chmod +x deploy.sh
                    ./deploy.sh ${IMAGE_TAG}
                '''
            }
        }
    }

    post {
        success {
            echo "Build completed successfully"
        }

        failure {
            echo "Build Failed"
        }

        always {
            cleanWs()
        }
    }
}
