pipeline{
    agent any

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
                -t traning-portal:latest .
               
                """
            }
        }

        stage("push to ECR"){
            steps{
                sh """ 
                aws ecr get-login-password --region us-east-1 | docker login \
                -- username AWS \
                -- password-stdin 110425445190.dkr.ecr.us-east-1.amazonaws.com

                docker tag training-portal:${IMAGE_TAG} \
                110425445190.dkr.ecr.us-east-1.amazonaws.com/training-portal:${IMAGE_TAG}

                docker tag training-portal:latest \
                110425445190.dkr.ecr.us-east-1.amazonaws.com/training-portal:latest
                
                docker push \
                110425445190.dkr.ecr.us-east-1.amazonaws.com/training-portal:${IMAGE_TAG}

                docker push \
                110425445190.dkr.ecr.us-east-1.amazonaws.com/training-portal:latest
                
                
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
