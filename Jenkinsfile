// Setup milestone to stop previous build from running when a new one is launched
// The result would be:
//  Build 1 runs and creates milestone 1
//  While build 1 is running, suppose build 2 fires. It has milestone 1 and milestone 2. It passes 1, which causes build #1 to abort

def buildNumber = env.BUILD_NUMBER as int
if (buildNumber > 1) milestone(buildNumber - 1)
milestone(buildNumber)
IMAGES = ["kogito-quarkus-ubi8", 
            "kogito-quarkus-jvm-ubi8",
            "kogito-quarkus-ubi8-s2i",
            "kogito-springboot-ubi8",
            "kogito-springboot-ubi8-s2i",
            "kogito-data-index",
            "kogito-jobs-service",
            "kogito-management-console"]

pipeline{
    agent { label 'jenkins-slave'}
    stages{
        stage('Initialization'){
            steps{
                script{
                    cleanWorkspaces()                    
                    
                    // Set the mirror url only if exist
                    if (env.MAVEN_MIRROR_REPOSITORY != null
                            && env.MAVEN_MIRROR_REPOSITORY != ''){
                        env.MAVEN_MIRROR_URL = env.MAVEN_MIRROR_REPOSITORY
                    }
                }
                sh "docker rm -f \$(docker ps -a -q) || date"
                sh "docker rmi -f \$(docker images -q) || date"
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
                    IMAGES.each{ image -> build_stages["${image}"] = {
                            createWorkspace("$image")
                            copyWorkspace("$image")
                            dir(getWorkspacePath("$image")){
                                try{
                                    sh "make ${image}"
                                }
                                finally{
                                    junit 'target/test/results/*.xml'
                                }
                            
                            }
                        }
                    }
                    parallel build_stages
                }
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
            script{
                cleanWorkspaces()
            }
        }
    }
}



void  createWorkspace(String image){
    sh "mkdir -p ${getWorkspacePath(image)}"
}
void copyWorkspace(String image){
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