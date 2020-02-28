@Library('jenkins-pipeline-shared-libraries')_

pipeline {
    agent {
        label 'kogito-static || kie-rhel7'
    }
    tools {
        maven 'kie-maven-3.5.4'
        jdk 'kie-jdk1.8'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
        timeout(time: 90, unit: 'MINUTES')
    }
    stages {
        stage('Initialize') {
            steps {
                sh 'printenv'
            }
        }
        stage('Build kogito-runtimes') {
            steps {
                dir("kogito-runtimes") {
                    script {
                        githubscm.checkoutIfExists('kogito-runtimes', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET")
                        maven.runMavenWithSubmarineSettings('clean install', true)
                    }
                }
            }
        }
        stage('Build kogito-cloud') {
            steps {
                script {
                    maven.runMavenWithSubmarineSettings('clean install', false)
                }
            }
        }
        stage('Build kogito-cloud-s2i') {
            steps {
                script {
                     build job: 'KIE/master/kogito-deploy/kogito-cloud-s2i-images-master',
                        propagate: true,
                        parameters: [
                            string(name: 'mainBranch', value: "$CHANGE_BRANCH"),
                            string(name: 'ghOrgUnit', value: "$CHANGE_AUTHOR")
                        ]
                }
            }
        }
    }
    post {
        unstable {
            script {
                mailer.sendEmailFailure()
            }
        }
        failure {
            script {
                mailer.sendEmailFailure()
            }
        }
        always {
            cleanWs()
        }
    }
}
