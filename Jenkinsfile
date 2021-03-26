@Library('jenkins-pipeline-shared-libraries')_

def changeAuthor = env.ghprbPullAuthorLogin ?: CHANGE_AUTHOR
def changeBranch = env.ghprbSourceBranch ?: CHANGE_BRANCH
def changeTarget = env.ghprbTargetBranch ?: CHANGE_TARGET

pipeline{
    agent { label 'kogito-image-slave && !master'}
    tools {
        jdk 'kie-jdk11'
    }
    options {
        timeout(time: 120, unit: 'MINUTES')
    }
    environment {
        CI = true
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

                    //Ignore self-signed certificates if MAVEN_MIRROR_URL is defined
                    if(env.MAVEN_MIRROR_URL != ''){
                        sh 'python3 scripts/update-tests.py --ignore-self-signed-cert'
                    }
                }
            }
        }
        stage('Validate CeKit Image and Modules descriptors'){
            steps {
                script {
                    sh '''
                        curl -Ls https://github.com/kiegroup/kie-cloud-tools/releases/download/v1.2/cekit-image-validator-runner.tgz --output cekit-image-validator-runner.tgz
                        tar -xzvf cekit-image-validator-runner.tgz
                        chmod +x cekit-image-validator-runner
                    '''
                    sh './cekit-image-validator-runner modules/'
                    sh './cekit-image-validator-runner image.yaml'
                    getImages().each{ image -> sh "./cekit-image-validator-runner ${image}-overrides.yaml" }   
                }
            }
        }
        stage('Prepare offline kogito-examples'){
            steps{
                sh "make clone-repos"
            }
        }
        stage('Build Images') {
            steps{
                script {
                    getImages().each{ image -> initWorkspace(image) }
                    launchParallelForEachImage("Build", {img -> buildImage(img)})
                }
            }
        }
        stage('Test Images') {
            steps {
                script {
                    launchParallelForEachImage("Test", {img -> testImage(img)})
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

void launchParallelForEachImage(stageNamePrefix, executeOnImage) {
    parallelStages = [:]
    getImages().each{ image -> 
        parallelStages["${stageNamePrefix} ${image}"] = {
            dir(getWorkspacePath(image)){
                executeOnImage(image)
            }
        }
    }
    parallel parallelStages
}

void buildImage(image) {
    sh "make build-image image_name=${image} ignore_test=true cekit_option='--work-dir .'"
}

void testImage(image) {
    try {
        sh "make build-image image_name=${image} ignore_build=true cekit_option='--work-dir .'"
    } finally {
        junit testResults: 'target/test/results/*.xml', allowEmptyResults: true
    }
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

String[] getImages(){
    return sh(script: "make list | tr '\\n' ','", returnStdout: true).trim().split(',')
}
