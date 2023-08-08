/*
* This file is describing all the Jenkins jobs in the DSL format (see https://plugins.jenkins.io/job-dsl/)
* needed by the Kogito pipelines.
*
* The main part of Jenkins job generation is defined into the https://github.com/kiegroup/kogito-pipelines repository.
*
* This file is making use of shared libraries defined in
* https://github.com/kiegroup/kogito-pipelines/tree/main/dsl/seed/src/main/groovy/org/kie/jenkins/jobdsl.
*/

import org.kie.jenkins.jobdsl.model.JobType
import org.kie.jenkins.jobdsl.utils.JobParamsUtils
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

// PR checks
setupPrJob()
setupDeployJob(JobType.PULL_REQUEST, 'kogito-bdd')

// Init branch
createSetupBranchJob()

// Nightly jobs
setupDeployJob(JobType.NIGHTLY)
KogitoJobUtils.createEnvironmentIntegrationBranchNightlyJob(this, 'quarkus-lts')

// Release jobs
setupDeployJob(JobType.RELEASE)
setupPromoteJob(JobType.RELEASE)

if (Utils.isProductizedBranch(this)) {
    setupPrJob(true) // Prod CI job
    setupProdUpdateVersionJob()
    setupQuarkusUpdateJob(true) // Prod CI job
}

// Update quarkus on community
setupQuarkusUpdateJob()

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupPrJob(boolean isProdCI = false) {
    setupBuildImageJob(JobType.PULL_REQUEST, '', isProdCI)

    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-images', JobType.PULL_REQUEST, "${jenkins_path}/Jenkinsfile", "Kogito Images${isProdCI ? ' Prod' : ''} PR check")
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.pr.putAll([
        run_only_for_branches: [ "${GIT_BRANCH}" ],
        disable_status_message_error: true,
        disable_status_message_failure: true,
        commitContext: 'Retrieve and Launch Image Checks',
        contextShowtestResults: false,
    ])
    if (isProdCI) {
        jobParams.job.name += '.prod'
        jobParams.pr.trigger_phrase = '.*[j|J]enkins,?.*(rerun|run) [prod|Prod|PROD].*'
        jobParams.pr.trigger_phrase_only = true
        jobParams.pr.commitContext = '(Prod) Retrieve and Launch Image Checks'
        jobParams.env.put('PROD_CI', true)
    } else if (Utils.hasBindingValue(this, 'CLOUD_IMAGES')) {
        jobParams.env.put('IMAGES_LIST', Utils.getBindingValue(this, 'CLOUD_IMAGES'))
    }
    jobParams.env.putAll([
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
    ])
    KogitoJobTemplate.createPRJob(this, jobParams)
}

void createSetupBranchJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-images', JobType.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'Kogito Images Init Branch')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'kogito-images',
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",

        IS_MAIN_BRANCH: "${Utils.isMainBranch(this)}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Release information
            stringParam('KOGITO_VERSION', '', 'Kogito version to set.')
            stringParam('KOGITO_ARTIFACTS_VERSION', '', 'Kogito Artifacts version to set')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupDeployJob(JobType jobType, String envName = '') {
    setupBuildImageJob(jobType, envName)

    def jobParams = JobParamsUtils.getBasicJobParamsWithEnv(this, 'kogito-images-deploy', jobType, envName, "${jenkins_path}/Jenkinsfile.deploy", 'Kogito Images Deploy')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    if (jobType == JobType.PULL_REQUEST) {
        jobParams.git.branch = '${BUILD_BRANCH_NAME}'
        jobParams.git.author = '${GIT_AUTHOR}'
        jobParams.git.project_url = Utils.createProjectUrl("${GIT_AUTHOR_NAME}", jobParams.git.repository)
    }
    jobParams.env.putAll([
        REPO_NAME: 'kogito-images',
        PROPERTIES_FILE_NAME: 'deployment.properties',

        MAX_REGISTRY_RETRIES: 3,

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
    ])
    if (jobType == JobType.PULL_REQUEST) {
        jobParams.env.putAll([
            MAVEN_ARTIFACT_REPOSITORY: "${MAVEN_PR_CHECKS_REPOSITORY_URL}",
        ])
    } else {
        jobParams.env.putAll([
            GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

            AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
            GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",

            MAVEN_ARTIFACT_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
            DEFAULT_STAGING_REPOSITORY: "${MAVEN_NEXUS_STAGING_PROFILE_URL}",

            QUARKUS_PLATFORM_NEXUS_URL: Utils.getMavenQuarkusPlatformRepositoryUrl(this),
        ])
    }
    if (Utils.hasBindingValue(this, 'CLOUD_IMAGES')) {
        jobParams.env.put('IMAGES_LIST', Utils.getBindingValue(this, 'CLOUD_IMAGES'))
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            if (jobType == JobType.PULL_REQUEST) {
                // author can be changed as param only for PR behavior, due to source branch/target, else it is considered as an env
                stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            }

            stringParam('APPS_URI', '', 'Git uri to the kogito-apps repository to use for tests.')
            stringParam('APPS_REF', '', 'Git reference (branch/tag) to the kogito-apps repository to use for building. Default to BUILD_BRANCH_NAME.')

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
            booleanParam('DEPLOY_WITH_LATEST_TAG', false, 'Set to true if you want the deployed images to also be with the `latest` tag')

            // Release information
            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('KOGITO_ARTIFACTS_VERSION', '', 'Optional. If artifacts\' version is different from PROJECT_VERSION.')
            if (jobType == JobType.RELEASE) {
                stringParam('QUARKUS_PLATFORM_VERSION', '', 'Allow to override the Quarkus Platform version')
            }

            stringParam('KOGITO_PR_BRANCH', '', 'PR branch name')
            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupBuildImageJob(JobType jobType, String envName = '', boolean prodCI = false) {
    def jobParams = JobParamsUtils.getBasicJobParamsWithEnv(this, 'kogito-images.build-image', jobType, envName, "${jenkins_path}/Jenkinsfile.build-image", 'Kogito Images Build single image')
    // Use jenkinsfile from the build branch
    jobParams.git.author = '${SOURCE_AUTHOR}'
    jobParams.git.branch = '${SOURCE_BRANCH}'
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        MAX_REGISTRY_RETRIES: 3,
        TARGET_AUTHOR: Utils.getGitAuthor(this), // In case of a PR to merge with target branch
        PROD_CI: prodCI,

        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        AUTHOR_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        logRotator {
            daysToKeep(10)
        }
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_IMAGE_NAME', '', 'Image name to build. Mandatory parameter.')

            stringParam('SOURCE_AUTHOR', Utils.getGitAuthor(this), 'Build author')
            stringParam('SOURCE_BRANCH', Utils.getGitBranch(this), 'Build branch name')
            stringParam('TARGET_BRANCH', '', '(Optional) In case of a PR to merge with target branch, please provide the target branch')

            // Build information
            stringParam('MAVEN_ARTIFACTS_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
            stringParam('BUILD_KOGITO_APPS_URI', '', '(Optional) Git uri to the kogito-apps repository to use for tests.')
            stringParam('BUILD_KOGITO_APPS_REF', '', '(Optional) Git reference (branch/tag) to the kogito-apps repository to use for building. Default to BUILD_BRANCH_NAME.')
            stringParam('QUARKUS_PLATFORM_URL', Utils.getMavenQuarkusPlatformRepositoryUrl(this), 'URL to the Quarkus platform to use. The version to use will be guessed from artifacts.')

            // Test information
            booleanParam('SKIP_TESTS', false, 'Skip tests')
            stringParam('TESTS_KOGITO_EXAMPLES_URI', '', '(Optional) Git uri to the kogito-examples repository to use for tests.')
            stringParam('TESTS_KOGITO_EXAMPLES_REF', '', '(Optional) Git reference (branch/tag) to the kogito-examples repository to use for tests.')

            // Deploy information
            booleanParam('DEPLOY_IMAGE', false, 'Should we deploy image to given deploy registry ?')
            booleanParam('DEPLOY_IMAGE_USE_OPENSHIFT_REGISTRY', false, 'Set to true if image should be deployed in Openshift registry.In this case, IMAGE_REGISTRY_CREDENTIALS, IMAGE_REGISTRY and IMAGE_NAMESPACE parameters will be ignored')
            stringParam('DEPLOY_IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS_NIGHTLY}", 'Image registry credentials to use to deploy images. Will be ignored if no IMAGE_REGISTRY is given')
            stringParam('DEPLOY_IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Image registry to use to deploy images')
            stringParam('DEPLOY_IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Image namespace to use to deploy images')
            stringParam('DEPLOY_IMAGE_NAME_SUFFIX', '', 'Image name suffix to use to deploy images. In case you need to change the final image name, you can add a suffix to it.')
            stringParam('DEPLOY_IMAGE_TAG', '', 'Image tag to use to deploy images')
            booleanParam('DEPLOY_WITH_LATEST_TAG', false, 'Set to true if you want the deployed images to also be with the `latest` tag')
        }
    }
}

void setupPromoteJob(JobType jobType) {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-images-promote', jobType, "${jenkins_path}/Jenkinsfile.promote", 'Kogito Images Promote')
    jobParams.env.putAll([
        REPO_NAME: 'kogito-images',
        PROPERTIES_FILE_NAME: 'deployment.properties',

        MAX_REGISTRY_RETRIES: 3,

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",

        DEFAULT_STAGING_REPOSITORY: "${MAVEN_NEXUS_STAGING_PROFILE_URL}",
        MAVEN_ARTIFACT_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
    ])
    if (Utils.hasBindingValue(this, 'CLOUD_IMAGES')) {
        jobParams.env.put('IMAGES_LIST', Utils.getBindingValue(this, 'CLOUD_IMAGES'))
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
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
            stringParam('BASE_IMAGE_NAMES', '', 'Override `deployment.properties`. Comma separated list of images')
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

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupProdUpdateVersionJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-images-update-prod-version', JobType.TOOLS, "${jenkins_path}/Jenkinsfile.update-prod-version", 'Update prod version for Kogito Images')
    jobParams.env.putAll([
        REPO_NAME: 'kogito-images',

        BUILD_BRANCH_NAME: "${GIT_BRANCH}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('JIRA_NUMBER', '', 'KIECLOUD-XXX or RHPAM-YYYY or else. This will be added to the commit and PR.')
            stringParam('PROD_PROJECT_VERSION', '', 'Which version to set ?')
        }
    }
}

void setupQuarkusUpdateJob(boolean isProdCI = false) {
    def prodFlag = isProdCI ? '--prod' : ''
    KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'kogito-images', [:], [:], [], [
        "source ~/virtenvs/cekit/bin/activate && python3 scripts/update-repository.py --quarkus-platform-version %new_version% ${prodFlag}"
    ])
}
