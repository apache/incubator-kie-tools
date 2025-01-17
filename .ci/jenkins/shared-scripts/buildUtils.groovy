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

/**
* Start Docker in Docker (DinD)
*/
def startDockerInDocker() {
    sh '''#!/bin/bash -el
    sudo entrypoint.sh
    sudo service dbus start
    '''.trim()
}

/**
* Start Xvfb X server required for KIE-Tools E2E tests
*/
def startXvfb() {
    sh '''#!/bin/bash -el
    Xvfb :99 -screen 0 1920x1080x24 > /dev/null 2>&1 &
    '''.trim()
}

/**
* Start Fluxbox window manager required for KIE-Tools E2E tests
*/
def startFluxbox() {
    sh '''#!/bin/bash -el
    fluxbox -display :99 > /dev/null 2>&1 &
    '''.trim()
}

/**
* Setup PNPM parameters for building KIE-Tools
*/
def setupPnpm(String mavenSettingsFileId = '') {
    sh """#!/bin/bash -el
    pnpm config set network-timeout 1000000
    pnpm -r exec 'bash' '-c' 'mkdir .mvn'
    pnpm -r exec 'bash' '-c' 'echo -B > .mvn/maven.config'
    pnpm -r exec 'bash' '-c' 'echo -Xmx2g > .mvn/jvm.config'
    """.trim()

    if (mavenSettingsFileId) {
        configFileProvider([configFile(fileId: mavenSettingsFileId, variable: 'MAVEN_SETTINGS_FILE')]) {
            sh """#!/bin/bash -el
            cp ${MAVEN_SETTINGS_FILE} ${WORKSPACE}/kie-settings.xml
            pnpm -r exec 'bash' '-c' 'echo --settings=${WORKSPACE}/kie-settings.xml >> .mvn/maven.config'
            """.trim()
        }
    }
}

/**
* PNPM Bootsrap
*/
def pnpmBootstrap(String filters = '', String mavenArgs = '') {
    sh """#!/bin/bash -el
    export MAVEN_ARGS=${mavenArgs}
    pnpm bootstrap ${filters}
    """.trim()
}

/**
* PNPM build all packages
*/
def pnpmBuildFull(Integer workspaceConcurrency = 1) {
    sh """#!/bin/bash -el
    pnpm -r --workspace-concurrency=${workspaceConcurrency} build:prod
    """.trim()
}

/**
* PNPM build a set of packages
*/
def pnpmBuild(String filters, Integer workspaceConcurrency = 1) {
    sh """#!/bin/bash -el
    pnpm ${filters} --workspace-concurrency=${workspaceConcurrency} build:prod
    """.trim()
}

/**
* PNPM update project version to
*/
def pnpmUpdateProjectVersion(String projectVersion) {
    sh """#!/bin/bash -el
    pnpm update-version-to ${projectVersion}
    """.trim()
}

/**
* PNPM update kogito version to
*/
def pnpmUpdateKogitoVersion(String kogitoVersion, String imagesTag) {
    sh """#!/bin/bash -el
    pnpm update-kogito-version-to --maven ${kogitoVersion}
    """.trim()
}

/**
* PNPM update stream name to
*/
def pnpmUpdateStreamName(String streamName) {
    sh """#!/bin/bash -el
    pnpm update-stream-name-to ${streamName}
    """.trim()
}

/**
* Start KIE-Tools required services for build and test
*/
def startRequiredServices() {
    startDockerInDocker()
    startXvfb()
    startFluxbox()
}

/**
* @return String build datetime - format (%Y-%m-%d %T)
*/
def buildDateTime() {
    return sh(script: "echo `date +'%Y-%m-%d %T'`", returnStdout: true).trim()
}

/**
* @return String the Apache Jenkins agent nodes with higher capacity (builds22 to builds30)
**/
def apacheAgentLabels() {
    return (22..30).collect { "builds$it" }.join(' || ')
}

return this
