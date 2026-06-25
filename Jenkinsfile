pipeline{

    agent none

    environment{
        APP_NAME = "devops-training-portal"

    }

    stages{
        stage("checkout on build-agent"){
            agent{
                label "build-agent"
            }
            steps{
                deleteDir()
                checkout scm
            }
        }

        stage("Maven build"){
            agent{
                label "build-agent"
            }
            steps{
                sh 'mvn clean package'
            }
        }

        stage("docker build test on build agent"){
            agent{
                label "build-agent"
            }
            steps{
                sh 'docker build -t devops-traning-portal:latest .' 
            }
        }

        stage('checkout on deploy agent'){
            agent{
                label "deploy-agent"

            }

            steps{
                deleteDir()
                checkout scm    
            }
        }

        stage("deploy on deploy agent"){
            agent{
                label "deploy-agent"
            }
            steps{
                sh ''' 
                    docker-compose down || true
                    docker-compose up -d --build
                
                '''
            }
        }
    }
    post {
        success{
            echo "Application built and deployed successfully using build-agent and deploy-agent"
        }

        failure{
            echo "Pipeline failed. Check stage logs."
        }
    }
}
