/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class PipelineVars implements Serializable {

    String githubRepositoryOrg = 'apache'
    String githubRepositoryName = 'incubator-kie-tools'
    String githubRepositorySlug = 'apache/incubator-kie-tools'
    String quayPushCredentialsId = 'quay-io-kie-tools-token'
    String dockerHubUserCredentialsId = 'DOCKERHUB_USER'
    String dockerHubTokenCredentialsId = 'DOCKERHUB_TOKEN'
    String openshiftCredentialsId = 'openshift-kie-tools-token'
    String kieToolsBotGithubCredentialsId = 'kie-tools-bot-gh'
    String kieToolsBotGithubTokenCredentialsId = 'kie-tools-bot-gh-token'
    String kieToolsGithubCodeQLTokenCredentialsId = 'kie-tools-gh-codeql-token'
    String chromeStoreCredentialsId = 'kie-tools-chrome-store'
    String chromeStoreRefreshTokenCredentialsId = 'kie-tools-chrome-store-refresh-token'
    String chromeExtensionIdCredentialsId = 'kie-tools-chrome-extension-id'
    String swfChromeExtensionIdCredentialsId = 'kie-tools-swf-chrome-extension-id'
    String npmTokenCredentialsId = 'kie-tools-npm-token'
    String buildKiteTokenCredentialsId = 'kie-tools-build-kite-token'
    String asfCIGithubCredentialsId = '399061d0-5ab5-4142-a186-a52081fef742'
    String asfGithubPushCredentialsId = '84811880-2025-45b6-a44c-2f33bef30ad2'
    String asfGithubTokenPushCredentialsId = '41128c14-bb63-4708-9074-d20a318ee630'
    String mavenSettingsConfigFileId = 'kie-release-settings'
    String mavenDeployRepositoryCredentialsId = 'apache-nexus-kie-deploy-credentials'
    String defaultArtifactsTempDir = 'artifacts-tmp'
    String asfReleaseStagingRepository = 'https://dist.apache.org/repos/dist/dev/incubator/kie'
    String asfReleaseGPGKeyCredentialsId = 'asf-release-gpg-signing-key'
    String asfReleaseGPGKeyPasswordCredentialsId = 'asf-release-gpg-signing-key-passphrase'
    String asfReleaseSVNStagingCredentialsId = 'asf-release-svn-staging'
    String kieToolsCiBuildImageRegistry = 'docker.io'
    String kieToolsCiBuildImageAccount = 'apache'
    String kieToolsCiBuildImageName = 'incubator-kie-tools-ci-build'

}

return new PipelineVars()
