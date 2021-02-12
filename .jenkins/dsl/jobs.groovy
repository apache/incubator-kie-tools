import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoConstants
import org.kie.jenkins.jobdsl.Utils
import org.kie.jenkins.jobdsl.KogitoJobType

boolean isMainBranch() {
    return "${GIT_BRANCH}" == "${GIT_MAIN_BRANCH}"
}

def getDefaultJobParams() {
    return [
        job: [
            name: 'kogito-images'
        ],
        git: [
            author: "${GIT_AUTHOR_NAME}",
            branch: "${GIT_BRANCH}",
            repository: 'kogito-images',
            credentials: "${GIT_AUTHOR_CREDENTIALS_ID}",
            token_credentials: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}"
        ]
    ]
}

def getJobParams(String jobName, String jobFolder, String jenkinsfileName, String jobDescription = '') {
    def jobParams = getDefaultJobParams()
    jobParams.job.name = jobName
    jobParams.job.folder = jobFolder
    jobParams.jenkinsfile = jenkinsfileName
    if (jobDescription) {
        jobParams.job.description = jobDescription
    }
    return jobParams
}

def bddRuntimesPrFolder = "${KogitoConstants.KOGITO_DSL_PULLREQUEST_FOLDER}/${KogitoConstants.KOGITO_DSL_RUNTIMES_BDD_FOLDER}"
def nightlyBranchFolder = "${KogitoConstants.KOGITO_DSL_NIGHTLY_FOLDER}/${JOB_BRANCH_FOLDER}"
def releaseBranchFolder = "${KogitoConstants.KOGITO_DSL_RELEASE_FOLDER}/${JOB_BRANCH_FOLDER}"

if (isMainBranch()) {
    folder(KogitoConstants.KOGITO_DSL_PULLREQUEST_FOLDER)

    setupPrJob(KogitoConstants.KOGITO_DSL_PULLREQUEST_FOLDER)

    // For BDD runtimes PR job
    folder(bddRuntimesPrFolder)

    setupDeployJob(bddRuntimesPrFolder, KogitoJobType.PR)
}

// Branch jobs
folder(KogitoConstants.KOGITO_DSL_NIGHTLY_FOLDER)
folder(nightlyBranchFolder)
setupDeployJob(nightlyBranchFolder, KogitoJobType.NIGHTLY)
setupPromoteJob(nightlyBranchFolder, KogitoJobType.NIGHTLY)

// No release directly on main branch
if (!isMainBranch()) {
    folder(KogitoConstants.KOGITO_DSL_RELEASE_FOLDER)
    folder(releaseBranchFolder)
    setupDeployJob(releaseBranchFolder, KogitoJobType.RELEASE)
    setupPromoteJob(releaseBranchFolder, KogitoJobType.RELEASE)
}

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupPrJob(String jobFolder) {
    def jobParams = getDefaultJobParams()
    jobParams.job.folder = jobFolder
    KogitoJobTemplate.createPRJob(this, jobParams)
}

void setupDeployJob(String jobFolder, KogitoJobType jobType) {
    def jobParams = getJobParams('kogito-images-deploy', jobFolder, 'Jenkinsfile.deploy', 'Kogito Images Deploy')
    if (jobType == KogitoJobType.PR) {
        jobParams.git.branch = '${GIT_BRANCH_NAME}'
        jobParams.git.author = '${GIT_AUTHOR}'
        jobParams.git.project_url = Utils.createProjectUrl("${GIT_AUTHOR_NAME}", jobParams.git.repository)
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            if (jobType == KogitoJobType.PR) {
                // author can be changed as param only for PR behavior, due to source branch/target, else it is considered as an env
                stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            }

            // Build&Test information
            booleanParam('SKIP_TESTS', false, 'Skip tests')
            stringParam('EXAMPLES_URI', '', 'Git uri to the kogito-examples repository to use for tests.')
            stringParam('EXAMPLES_REF', '', 'Git reference (branch/tag) to the kogito-examples repository to use for tests.')

            // Deploy information
            booleanParam('IMAGE_USE_OPENSHIFT_REGISTRY', false, 'Set to true if image should be deployed in Openshift registry.In this case, IMAGE_REGISTRY_CREDENTIALS, IMAGE_REGISTRY and IMAGE_NAMESPACE parameters will be ignored')
            stringParam('IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS_NIGHTLY}", 'Image registry credentials to use to deploy images. Will be ignored if no IMAGE_REGISTRY is given')
            stringParam('IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Image registry to use to deploy images')
            stringParam('IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Image namespace to use to deploy images')
            stringParam('IMAGE_NAME_SUFFIX', '', 'Image name suffix to use to deploy images. In case you need to change the final image name, you can add a suffix to it.')
            stringParam('IMAGE_TAG', '', 'Image tag to use to deploy images')

            // Release information
            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('KOGITO_ARTIFACTS_VERSION', '', 'Optional. If artifacts\' version is different from PROJECT_VERSION.')

            booleanParam('CREATE_PR', false, 'In case of not releasing, you can ask to create a PR with the changes')
        }

        environmentVariables {
            env('CI', true)
            env('REPO_NAME', 'kogito-images')
            env('PROPERTIES_FILE_NAME', 'deployment.properties')

            env('CONTAINER_ENGINE', 'docker')
            env('CONTAINER_TLS_OPTIONS', '')
            env('MAX_REGISTRY_RETRIES', 3)

            env('RELEASE', jobType == KogitoJobType.RELEASE)
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")

            if (jobType == KogitoJobType.PR) {
                env('MAVEN_ARTIFACT_REPOSITORY', "${MAVEN_PR_CHECKS_REPOSITORY_URL}")
            } else {
                env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")

                env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
                env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")
                env('GIT_AUTHOR_BOT', "${GIT_BOT_AUTHOR_NAME}")
                env('BOT_CREDENTIALS_ID', "${GIT_BOT_AUTHOR_CREDENTIALS_ID}")

                env('MAVEN_ARTIFACT_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
                env('DEFAULT_STAGING_REPOSITORY', "${MAVEN_NEXUS_STAGING_PROFILE_URL}")
            }
        }
    }
}

void setupPromoteJob(String jobFolder, KogitoJobType jobType) {
    KogitoJobTemplate.createPipelineJob(this, getJobParams('kogito-images-promote', jobFolder, 'Jenkinsfile.promote', 'Kogito Images Promote')).with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Deploy job url to retrieve deployment.properties
            stringParam('DEPLOY_BUILD_URL', '', 'URL to jenkins deploy build to retrieve the `deployment.properties` file. If base parameters are defined, they will override the `deployment.properties` information')

            // Base images information which can override `deployment.properties`
            booleanParam('BASE_IMAGE_USE_OPENSHIFT_REGISTRY', false, 'Override `deployment.properties`. Set to true if base image should be retrieved from Openshift registry.In this case, BASE_IMAGE_REGISTRY_CREDENTIALS, BASE_IMAGE_REGISTRY and BASE_IMAGE_NAMESPACE parameters will be ignored')
            stringParam('BASE_IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS_NIGHTLY}", 'Override `deployment.properties`. Base Image registry credentials to use to deploy images. Will be ignored if no BASE_IMAGE_REGISTRY is given')
            stringParam('BASE_IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Override `deployment.properties`. Base image registry')
            stringParam('BASE_IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Override `deployment.properties`. Base image namespace')
            stringParam('BASE_IMAGE_NAME_SUFFIX', '', 'Override `deployment.properties`. Base image name suffix')
            stringParam('BASE_IMAGE_TAG', '', 'Override `deployment.properties`. Base image tag')

            // Promote images information
            booleanParam('PROMOTE_IMAGE_USE_OPENSHIFT_REGISTRY', false, 'Set to true if base image should be deployed in Openshift registry.In this case, PROMOTE_IMAGE_REGISTRY_CREDENTIALS, PROMOTE_IMAGE_REGISTRY and PROMOTE_IMAGE_NAMESPACE parameters will be ignored')
            stringParam('PROMOTE_IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS_NIGHTLY}", 'Promote Image registry credentials to use to deploy images. Will be ignored if no PROMOTE_IMAGE_REGISTRY is given')
            stringParam('PROMOTE_IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Promote image registry')
            stringParam('PROMOTE_IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Promote image namespace')
            stringParam('PROMOTE_IMAGE_NAME_SUFFIX', '', 'Promote image name suffix')
            stringParam('PROMOTE_IMAGE_TAG', '', 'Promote image tag')
            booleanParam('DEPLOY_WITH_LATEST_TAG', false, 'Set to true if you want the deployed images to also be with the `latest` tag')

            // Release information which can override `deployment.properties`
            stringParam('PROJECT_VERSION', '', 'Override `deployment.properties`. Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('KOGITO_ARTIFACTS_VERSION', '', 'Optional. If artifacts\' version is different from PROJECT_VERSION.')
            stringParam('GIT_TAG', '', 'Git tag to set, if different from PROJECT_VERSION')
            stringParam('RELEASE_NOTES', '', 'Release notes to be added. If none provided, a default one will be given.')
        }

        environmentVariables {
            env('CI', true)
            env('REPO_NAME', 'kogito-images')
            env('PROPERTIES_FILE_NAME', 'deployment.properties')

            env('CONTAINER_ENGINE', 'podman')
            env('CONTAINER_TLS_OPTIONS', '--tls-verify=false')
            env('MAX_REGISTRY_RETRIES', 3)

            env('RELEASE', jobType == KogitoJobType.RELEASE)
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")

            env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")

            env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
            env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")
            env('GIT_AUTHOR_BOT', "${GIT_BOT_AUTHOR_NAME}")
            env('BOT_CREDENTIALS_ID', "${GIT_BOT_AUTHOR_CREDENTIALS_ID}")

            env('DEFAULT_STAGING_REPOSITORY', "${MAVEN_NEXUS_STAGING_PROFILE_URL}")
            env('MAVEN_ARTIFACT_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
        }
    }
}
