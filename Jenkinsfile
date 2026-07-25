pipeline{
    agent any

    tools{
        maven 'Maven-3.9'
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
