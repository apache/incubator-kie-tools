@Library('jenkins-pipeline-shared-libraries')_

def changeAuthor = env.ghprbPullAuthorLogin ?: CHANGE_AUTHOR
def changeBranch = env.ghprbSourceBranch ?: CHANGE_BRANCH
def changeTarget = env.ghprbTargetBranch ?: CHANGE_TARGET

IMAGES = ["kogito-quarkus-ubi8", 
            "kogito-quarkus-jvm-ubi8",
            "kogito-quarkus-ubi8-s2i",
            "kogito-springboot-ubi8",
            "kogito-springboot-ubi8-s2i",
            "kogito-data-index",
            "kogito-jobs-service",
            "kogito-management-console"]

pipeline{
    agent { label 'kogito-image-slave && !master'}
    tools {
        jdk 'kie-jdk11'
    }
    environment {
        JAVA_HOME = "${GRAALVM_HOME}"
    }
    stages{
        stage('Initialization'){
            steps{
                script{
                    clean()
                    
                    // Set the mirror url only if exist
                    if (env.MAVEN_MIRROR_REPOSITORY != null
                            && env.MAVEN_MIRROR_REPOSITORY != ''){
                        env.MAVEN_MIRROR_URL = env.MAVEN_MIRROR_REPOSITORY
                    }

                    githubscm.checkoutIfExists('kogito-images', changeAuthor, changeBranch, 'kiegroup', changeTarget, true)
                }
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
        stage('Build and Test Images'){
            steps{
                script {
                    build_stages = [:]
                    IMAGES.each{ image -> build_stages[image] = {
                            initWorkspace(image)
                            dir(getWorkspacePath(image)){
                                try{
                                    sh "make ${image} cekit_option='--work-dir .'"
                                }
                                finally{
                                    junit testResults: 'target/test/results/*.xml', allowEmptyResults: true
                                }
                            
                            }
                        }
                    }
                    parallel build_stages
                }
            }
        }
    }
    post{
        always{
            script{
                clean()
            }
        }
    }
}

void clean() {
    cleanWorkspaces()
    cleanImages()

    // Clean Cekit cache, in case we reuse an old node
    sh "rm -rf \$HOME/.cekit/cache"
}

void cleanImages(){
    sh "docker rm -f \$(docker ps -a -q) || date"
    sh "docker rmi -f \$(docker images -q) || date"
}


void initWorkspace(String image){
    sh "mkdir -p ${getWorkspacePath(image)}"
    sh "rsync -av --progress . ${getWorkspacePath(image)} --exclude workspaces"
}

void cleanWorkspaces(){
    sh "rm -rf ${getWorkspacesPath()}"
}

String getWorkspacesPath(){
    return "${WORKSPACE}/workspaces"
}

String getWorkspacePath(String image){
    return "${getWorkspacesPath()}/${image}"
}