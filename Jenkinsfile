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
                script{
                    sh ''' 
                     
                      docker build -t training-portal:latest .
                    
                    '''
                }
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
