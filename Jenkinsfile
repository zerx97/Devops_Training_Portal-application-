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
