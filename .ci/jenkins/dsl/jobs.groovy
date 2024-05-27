/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
 
/*
* This file is describing all the Jenkins jobs in the DSL format (see https://plugins.jenkins.io/job-dsl/)
* needed by the Kogito pipelines.
*
* The main part of Jenkins job generation is defined into the https://github.com/apache/incubator-kie-kogito-pipelines repository.
*
* This file is making use of shared libraries defined in
* https://github.com/apache/incubator-kie-kogito-pipelines/tree/main/dsl/seed/src/main/groovy/org/kie/jenkins/jobdsl.
*/

import org.kie.jenkins.jobdsl.model.JobType
import org.kie.jenkins.jobdsl.utils.JobParamsUtils
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

// PR checks
setupPrJob()

// Init branch
createSetupBranchJob()

// Nightly jobs
setupDeployJob(JobType.NIGHTLY)
KogitoJobUtils.createEnvironmentIntegrationBranchNightlyJob(this, 'quarkus-lts')

// Weekly jobs
setupWeeklyDeployJob(JobType.OTHER)

// Release jobs
setupDeployJob(JobType.RELEASE)
setupPromoteJob(JobType.RELEASE)

// Update quarkus on community
setupQuarkusUpdateJob()

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupPrJob() {
    setupBuildImageJob(JobType.PULL_REQUEST)
    setupBuildAndTestJob(JobType.PULL_REQUEST)

    // Branch Source Plugin multibranchPipelineJob
    Utils.isMainBranch(this) && KogitoJobTemplate.createPullRequestMultibranchPipelineJob(this, "${jenkins_path}/Jenkinsfile", JobType.PULL_REQUEST.getName())
}

void createSetupBranchJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-images', JobType.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'Kogito Images Init Branch')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

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

void setupDeployJob(JobType jobType) {
    setupBuildImageJob(jobType)

    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-images-deploy', jobType, "${jenkins_path}/Jenkinsfile.deploy", 'Kogito Images Deploy')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',

        MAX_REGISTRY_RETRIES: 3,

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_ARTIFACT_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        DEFAULT_STAGING_REPOSITORY: "${MAVEN_NEXUS_STAGING_PROFILE_URL}",

        QUARKUS_PLATFORM_NEXUS_URL: Utils.getMavenQuarkusPlatformRepositoryUrl(this),

        DISABLE_IMAGES_DEPLOY: (jobType==JobType.NIGHTLY) ? true : Utils.isImagesDeployDisabled(this)
    ])
    if (Utils.hasBindingValue(this, 'CLOUD_IMAGES')) {
        jobParams.env.put('IMAGES_LIST', Utils.getBindingValue(this, 'CLOUD_IMAGES'))
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('APPS_URI', '', 'Git uri to the kogito-apps repository to use for tests.')
            stringParam('APPS_REF', '', 'Git reference (branch/tag) to the kogito-apps repository to use for building. Default to BUILD_BRANCH_NAME.')

            // Build&Test information
            booleanParam('SKIP_TESTS', false, 'Skip tests')
            stringParam('EXAMPLES_URI', '', 'Git uri to the kogito-examples repository to use for tests.')
            stringParam('EXAMPLES_REF', '', 'Git reference (branch/tag) to the kogito-examples repository to use for tests.')

            // Deploy information
            booleanParam('IMAGE_USE_OPENSHIFT_REGISTRY', false, 'Set to true if image should be deployed in Openshift registry.In this case, IMAGE_REGISTRY_CREDENTIALS, IMAGE_REGISTRY and IMAGE_NAMESPACE parameters will be ignored')
            stringParam('IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS}", 'Image registry credentials to use to deploy images. Will be ignored if no IMAGE_REGISTRY is given')
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

void setupBuildImageJob(JobType jobType) {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-images.build-image', jobType, "${jenkins_path}/Jenkinsfile.build-image", 'Kogito Images Build single image')
    // Use jenkinsfile from the build branch
    jobParams.git.author = '${SOURCE_AUTHOR}'
    jobParams.git.branch = '${SOURCE_BRANCH}'
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        MAX_REGISTRY_RETRIES: 3,
        TARGET_AUTHOR: Utils.getGitAuthor(this), // In case of a PR to merge with target branch

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",

        RELEASE_GPG_SIGN_KEY_CREDS_ID: Utils.getReleaseGpgSignKeyCredentialsId(this),
        RELEASE_GPG_SIGN_PASSPHRASE_CREDS_ID: Utils.getReleaseGpgSignPassphraseCredentialsId(this),
        RELEASE_SVN_REPOSITORY: Utils.getReleaseSvnCredentialsId(this),
        RELEASE_SVN_CREDS_ID: Utils.getReleaseSvnStagingRepository(this)
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
            stringParam('UPDATE_KOGITO_VERSION', '', '(Optional) Update kogito to a specific version in the project')

            // Test information
            booleanParam('SKIP_TESTS', false, 'Skip tests')
            stringParam('TESTS_KOGITO_EXAMPLES_URI', '', '(Optional) Git uri to the kogito-examples repository to use for tests.')
            stringParam('TESTS_KOGITO_EXAMPLES_REF', '', '(Optional) Git reference (branch/tag) to the kogito-examples repository to use for tests.')

            // Deploy information
            booleanParam('DEPLOY_IMAGE', false, 'Should we deploy image to given deploy registry ?')
            booleanParam('DEPLOY_IMAGE_USE_OPENSHIFT_REGISTRY', false, 'Set to true if image should be deployed in Openshift registry.In this case, IMAGE_REGISTRY_CREDENTIALS, IMAGE_REGISTRY and IMAGE_NAMESPACE parameters will be ignored')
            stringParam('DEPLOY_IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS}", 'Image registry credentials to use to deploy images. Will be ignored if no IMAGE_REGISTRY is given')
            stringParam('DEPLOY_IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Image registry to use to deploy images')
            stringParam('DEPLOY_IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Image namespace to use to deploy images')
            stringParam('DEPLOY_IMAGE_NAME_SUFFIX', '', 'Image name suffix to use to deploy images. In case you need to change the final image name, you can add a suffix to it.')
            stringParam('DEPLOY_IMAGE_TAG', '', 'Image tag to use to deploy images')
            booleanParam('DEPLOY_WITH_LATEST_TAG', false, 'Set to true if you want the deployed images to also be with the `latest` tag')
            booleanParam('EXPORT_AND_GPG_SIGN_IMAGE', jobType == JobType.RELEASE, 'Set to true if should images be exported and signed.')
        }
    }
}

void setupBuildAndTestJob(JobType jobType) {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-images.build-and-test', jobType, "${jenkins_path}/Jenkinsfile.build-and-test", 'Kogito Images Build And test images')
    // Use jenkinsfile from the build branch
    jobParams.git.author = '${SOURCE_AUTHOR}'
    jobParams.git.repository = '${SOURCE_REPOSITORY}'
    jobParams.git.branch = '${SOURCE_BRANCH}'
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        MAX_REGISTRY_RETRIES: 3,
        TARGET_AUTHOR: Utils.getGitAuthor(this), // In case of a PR to merge with target branch

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",
    ])
    if (Utils.hasBindingValue(this, 'CLOUD_IMAGES')) {
        jobParams.env.put('IMAGES_LIST', Utils.getBindingValue(this, 'CLOUD_IMAGES'))
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        logRotator {
            daysToKeep(10)
        }
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('SOURCE_AUTHOR', Utils.getGitAuthor(this), 'Build author')
            stringParam('SOURCE_REPOSITORY', Utils.getRepoName(this), 'Build repository name')
            stringParam('SOURCE_BRANCH', Utils.getGitBranch(this), 'Build branch name')
            stringParam('TARGET_BRANCH', '', '(Optional) In case of a PR to merge with target branch, please provide the target branch')
            stringParam('CHANGE_ID', '', 'CHANGE_ID coming from Branch Source Plugin')
            stringParam('CHANGE_URL', '', 'CHANGE_URL coming from Branch Source Plugin')

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
            stringParam('DEPLOY_IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS}", 'Image registry credentials to use to deploy images. Will be ignored if no IMAGE_REGISTRY is given')
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
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',

        MAX_REGISTRY_RETRIES: 3,

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

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
            stringParam('BASE_IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS}", 'Override `deployment.properties`. Base Image registry credentials to use to deploy images. Will be ignored if no BASE_IMAGE_REGISTRY is given')
            stringParam('BASE_IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Override `deployment.properties`. Base image registry')
            stringParam('BASE_IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Override `deployment.properties`. Base image namespace')
            stringParam('BASE_IMAGE_NAMES', '', 'Override `deployment.properties`. Comma separated list of images')
            stringParam('BASE_IMAGE_NAME_SUFFIX', '', 'Override `deployment.properties`. Base image name suffix')
            stringParam('BASE_IMAGE_TAG', '', 'Override `deployment.properties`. Base image tag')

            // Promote images information
            booleanParam('PROMOTE_IMAGE_USE_OPENSHIFT_REGISTRY', false, 'Set to true if base image should be deployed in Openshift registry.In this case, PROMOTE_IMAGE_REGISTRY_CREDENTIALS, PROMOTE_IMAGE_REGISTRY and PROMOTE_IMAGE_NAMESPACE parameters will be ignored')
            stringParam('PROMOTE_IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS}", 'Promote Image registry credentials to use to deploy images. Will be ignored if no PROMOTE_IMAGE_REGISTRY is given')
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

void setupQuarkusUpdateJob() {
    KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'kogito-images', [:], [:], [], [
        "python scripts/update-repository.py --quarkus-platform-version %new_version%"
    ])
}

void setupWeeklyDeployJob(JobType jobType) {
    setupBuildImageJob(jobType)

    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-images.weekly-deploy', jobType, "${jenkins_path}/Jenkinsfile.weekly.deploy", 'Kogito Images Weekly Deploy')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',

        MAX_REGISTRY_RETRIES: 3,

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_ARTIFACT_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        DEFAULT_STAGING_REPOSITORY: "${MAVEN_NEXUS_STAGING_PROFILE_URL}",

        QUARKUS_PLATFORM_NEXUS_URL: Utils.getMavenQuarkusPlatformRepositoryUrl(this),
    ])
    if (Utils.hasBindingValue(this, 'CLOUD_IMAGES')) {
        jobParams.env.put('IMAGES_LIST', Utils.getBindingValue(this, 'CLOUD_IMAGES'))
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('APPS_URI', '', 'Git uri to the kogito-apps repository to use for tests.')
            stringParam('APPS_REF', '', 'Git reference (branch/tag) to the kogito-apps repository to use for building. Default to BUILD_BRANCH_NAME.')

            // Build&Test information
            booleanParam('SKIP_TESTS', false, 'Skip tests')
            stringParam('EXAMPLES_URI', '', 'Git uri to the kogito-examples repository to use for tests.')
            stringParam('EXAMPLES_REF', '', 'Git reference (branch/tag) to the kogito-examples repository to use for tests.')

            // Deploy information
            booleanParam('IMAGE_USE_OPENSHIFT_REGISTRY', false, 'Set to true if image should be deployed in Openshift registry.In this case, IMAGE_REGISTRY_CREDENTIALS, IMAGE_REGISTRY and IMAGE_NAMESPACE parameters will be ignored')
            stringParam('IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS}", 'Image registry credentials to use to deploy images. Will be ignored if no IMAGE_REGISTRY is given')
            stringParam('IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Image registry to use to deploy images')
            stringParam('IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Image namespace to use to deploy images')
            stringParam('IMAGE_NAME_SUFFIX', '', 'Image name suffix to use to deploy images. In case you need to change the final image name, you can add a suffix to it.')
            booleanParam('DEPLOY_WITH_LATEST_TAG', false, 'Set to true if you want the deployed images to also be with the `latest` tag')

            stringParam('GIT_CHECKOUT_DATETIME', '', 'Git checkout date and time - (Y-m-d H:i)')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}
