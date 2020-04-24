pipeline{
    agent { label 'jenkins-slave'}
    stages{
        stage('Initialization'){
            steps{
                sh "docker rmi -f \$(docker images -q) || date"
            }
        }
        stage('Prepare offline kogito-examples'){
            steps{
                sh "make clone-repos"
            }
        }
        stage('Build and test kogito-quarkus-ubi8 image'){
            steps{
                sh "make kogito-quarkus-ubi8"
            }
        }
        stage('Build and test kogito-quarkus-jvm-ubi8 image'){
            steps{
                sh "make kogito-quarkus-jvm-ubi8"
            }
        }
        stage('Build and test kogito-quarkus-ubi8-s2i image'){
            steps{
                sh "make kogito-quarkus-ubi8-s2i"
            }
        }
        stage('Build and test kogito-springboot-ubi8 image'){
            steps{
                sh "make kogito-springboot-ubi8"
            }
        }
        stage('Build and test kogito-springboot-ubi8-s2i image '){
            steps{
                sh "make kogito-springboot-ubi8-s2i"
            }
        }
        stage('Build and test kogito-data-index image '){
            steps{
                sh "make kogito-data-index"
            }
        }
        stage('Build and test kogito-jobs-service image '){
            steps{
                sh "make kogito-jobs-service"
            }
        }
        stage('Build and test kogito-management-console image '){
            steps{
                sh "make kogito-management-console"
            }
        }
        stage('Finishing'){
            steps{
                sh "docker rmi -f \$(docker images -q) || date"
            }
        }
    }
    post{
        always{
            junit 'target/test/results/*.xml'
        }
    }
}

