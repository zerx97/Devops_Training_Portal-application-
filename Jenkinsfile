pipeline{

    agent {
        label "build-agent"
    }

    environment{
        APP_NAME = "devops-training-portal"
        AWS_REGION = "us-east-1"
        ECR_REGISTRY = "110425445190.dkr.ecr.us-east-1.amazonaws.com"
    }
    tools {
        maven 'Maven-3.9'   // must match the name you gave in Jenkins Tool
    }

    stages{
        stage("checkout"){
            steps{
                deleteDir()
                checkout scm
            }
        }

        // Gets short git commit hash automatically
        // No manual input needed ever
        stage("Getting Git SHA as image-tag"){
            steps{
                script{
                    def gitCommit = sh(
                    script: "git rev-parse --short HEAD",
                    returnStdout: true
                    ).trim()
                    env.IMAGE_TAG = gitCommit
                    echo "Image tag set to: ${env.IMAGE_TAG}"
                }
            }
        }
       

        stage("Maven build"){
            steps{
                sh "mvn clean package -DskipTests"
            }
        }

        stage("docker build"){  
            steps{
                sh """ 
                docker build -t ${APP_NAME}:${IMAGE_TAG} .
                docker tag ${APP_NAME}:${IMAGE_TAG} ${ECR_REGISTRY}/${APP_NAME}:${IMAGE_TAG}
                docker tag ${APP_NAME}:${IMAGE_TAG} ${ECR_REGISTRY}/${APP_NAME}:latest
                """
            }
        }
        
        //Login to ECR using IAM Role (no password needed)
        //Push both tags
        stage("Push to ECR"){
            steps{
                sh """ 
                aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
               
                docker push ${ECR_REGISTRY}/${APP_NAME}:${IMAGE_TAG}
                docker push ${ECR_REGISTRY}/${APP_NAME}:latest
                
                """
            }
        }

        stage("deploy on build agent"){
            steps{
                sh """
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
                    export IMAGE_URI=${ECR_REGISTRY}/${APP_NAME}:${IMAGE_TAG}
                    docker-compose down || true
                    docker-compose up -d
                
                """
            }
        }
    }
    post {
        success{
            echo "✅ Built, pushed to ECR and deployed successfully! Build #${BUILD_NUMBER}"

            echo "✅ Application built and deployed successfully using build-agent"
        }

        failure{
            echo "❌ Pipeline failed. Check stage logs."
        }
        always {
            // Clean up old images to save disk space
            sh 'docker system prune -f --filter "until=24h"'
        }
    }
}
