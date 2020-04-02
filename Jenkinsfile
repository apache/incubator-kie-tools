pipeline{
    agent { label 'jenkins-slave'}
    stages{
        stage('Initialization'){
            steps{
                sh "docker rmi -f \$(docker images -q) || date"
            }
        }
        stage('Build'){
            steps{
                sh "make build"
            }
        }
        stage('Test'){
            steps{
                sh "make test"
            }
            post{
                always{
                    junit 'target/test/results/*.xml'
                }
            }
        }
        stage('Finishing'){
            steps{
                sh "docker rmi -f \$(docker images -q) || date"
            }
        }
    }
}

