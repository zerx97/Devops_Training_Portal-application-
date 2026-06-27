pipeline{

    agent {
        label "build-agent"
    }

    environment{
        APP_NAME = "devops-training-portal"
        AWS_REGION = "us-east-1"
        ECR_REGISTRY = "110425445190.dkr.ecr.us-east-1.amazonaws.com"
        SECRET_NAME = "devops-training-portal/db-credentials"
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

        stage("Fetch Secrets"){

            steps{
                script{

                    // Fetch entire secret as JSON string
                    def secretJson = sh (
                            script: """ 
                            aws secretmanager get-secret-value --secret-id ${SECRET_NAME} --region ${AWS-REGION} \
                            --query SecretString --output text
                        """,
                        returnStdout: true
                    ).trim()

                    // Parse JSON and set each value as env variable
                    def secrets = readJSON text: secretJson
 
                    evn.MYSQL_ROOT_PASSWORD        = secrets.MYSQL_ROOT_PASSWORD
                    env.MYSQL_DATABASE             = secrets.MYSQL_DATABASE
                    env.MYSQL_USER                 = secrets.MYSQL_USER
                    env.MYSQL_PASSWORD             = secrets.MYSQL_PASSWORD
                    env.SPRING_DATASOURCE_URL      = secrets.SPRING_DATASOURCE_URL
                    env.SPRING_DATASOURCE_USERNAME = secrets.SPRING_DATASOURCE_USERNAME
                    env.SPRING_DATASOURCE_PASSWORD = secrets.SPRING_DATASOURCE_PASSWORD
                    echo "✅ Secrets fetched successfully"
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
                    
                    echo "Deploying image: ${ECR_REGISTRY}/${APP_NAME}:${IMAGE_TAG}"
                    
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
