pipeline{
    agent { label 'jenkins-slave'}
    stages{
        stage('Initialization'){
            steps{
                sh "docker rm -f \$(docker ps -a -q) || docker rmi -f \$(docker images -q) || date"
            }
        }
        stage('Validate CeKit Image and Modules descriptors'){
            steps {
                sh """
                    curl -Ls https://github.com/kiegroup/kie-cloud-tools/releases/download/1.0-SNAPSHOT/cekit-image-validator-runner.tgz --output cekit-image-validator-runner.tgz
                    tar -xzvf cekit-image-validator-runner.tgz
                    chmod +x cekit-image-validator-runner
                """
                sh "./cekit-image-validator-runner modules/"
                sh """
                    ./cekit-image-validator-runner image.yaml
                    ./cekit-image-validator-runner kogito-data-index-overrides.yaml
                    ./cekit-image-validator-runner kogito-jobs-service-overrides.yaml
                    ./cekit-image-validator-runner kogito-management-console-overrides.yaml
                    ./cekit-image-validator-runner kogito-quarkus-jvm-overrides.yaml
                    ./cekit-image-validator-runner kogito-quarkus-overrides.yaml
                    ./cekit-image-validator-runner kogito-quarkus-s2i-overrides.yaml
                    ./cekit-image-validator-runner kogito-springboot-overrides.yaml
                    ./cekit-image-validator-runner kogito-springboot-s2i-overrides.yaml
                """
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

