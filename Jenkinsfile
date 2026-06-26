pipeline{

    agent {
        label "build-agent"
    }

    environment{
        APP_NAME = "devops-training-portal"

    }
    tools {
        maven 'Maven-3.9'   // must match the name you gave in Jenkins Tool'
    }

    stages{
        stage("checkout"){
            steps{
                deleteDir()
                checkout scm
            }
        }

        stage("Maven build"){
            steps{
                sh 'mvn clean package -DskipTests'
            }
        }

        stage("docker build"){  
            steps{
                sh "docker build -t ${APP_NAME}:latest ." 
            }
        }

        stage("deploy on deploy agent"){
            steps{
                sh ''' 
                    docker-compose down || true
                    docker-compose up -d
                
                '''
            }
        }
    }
    post {
        success{
            echo "Application built and deployed successfully using build-agent"
        }

        failure{
            echo "Pipeline failed. Check stage logs."
        }
    }
}
