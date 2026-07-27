pipeline{
    agent any

    environment{
        APP_NAME = "training-portal"
        AWS-REGION = "us-east-1"
        ECR_REGISTRY = "110425445190.dkr.ecr.us-east-1.amazonaws.com"
    }

    tools{
        maven 'Maven'
    }

    stages{
        stage("checkout"){
            steps{
                checkout scm 
            }
        }

        stage("get git commit"){
            steps{
                script{
                    env.IMAGE_TAG = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                }
            }
        }

        stage("build"){
            steps{
                sh 'mvn clean package -DskipTests'
            }
        }

        stage("archive artifact"){
            steps{
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage("build docker image"){
            steps{
               sh """ 
                docker build -t training-portal:${IMAGE_TAG} \
                -t training-portal:latest .
               
                """
            }
        }

        stage("login to ECR"){
            steps{
                sh """
                  aws ecr get-login-password --region ${AWS_REGION} | docker login \
                --username AWS \
                --password-stdin ${ECR_REGISTRY}
                
                 """ 
            }
        }
        
        stage("Tag docker image"){
            steps{
                sh """
                docker tag ${APP_NAME}:${IMAGE_TAG} \
                ${ECR_REGISTRY}/${APP_NAME}:${IMAGE_TAG}

                docker tag ${APP_NAME}:latest \
                ${ECR_REGISTRY}/${APP_NAME}:latest
                
                 """
            }
        }

        stage("Push Image to Amazon ECR"){
            steps{
                sh """ 
                
                docker push \
                ${ECR_REGISTRY}/${APP_NAME}:${IMAGE_TAG}

                docker push \
                ${ECR_REGISTRY}/${APP_NAME}:latest
                
                """
            }
        }
    }

    post{
        success{
            echo "Build completed successfully"
        }

        failure{
            echo "Build Failed"
        }

        always{
            cleanWs()
        }
    }
}
