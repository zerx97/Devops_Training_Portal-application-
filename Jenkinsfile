pipeline{

    agent any 

    environment{
        APP_CONTAINER = "portal_app"
        MYSQL_CONTAINER = "portal_mysql"
    }


    stages{

        stage("checkout"){
            steps{
                git branch: "main",
                url: "https://github.com/zerx97/Devops_Training_Portal-application-.git"
            }
        }

        stage("Maven Build"){
            steps{

                sh 'mvn clean package'
            }
        }

        stage("Deploy Containers"){
            steps{
                sh ''' 
                    docker-compose down || true

                    docker-compose up -d --build

                '''
            }
        }

        stage("verify containers"){
            steps{
                sh 'docker-compose ps'
            }
        }

        stage("Application health check"){
            steps{
                sh ''' 
                    echo "waiting for application to come up..."
                    sleep 20
                    curl -f http://localhost:8081 || exit 1

                '''
            }
        }
    }

    post{
        success{
            echo "Application deployed successfully"
        }
        failure{
            echo "Deployment failed.. Fetching logs ..."
            sh "docker-compose ps || true"
            sh "docker-compose logs portal_app || true"   
            sh "docker-compose logs portal_mysql || true" 
        }
        always{
            echo "*********Pipeline Finished*********"
        }
    }

}
