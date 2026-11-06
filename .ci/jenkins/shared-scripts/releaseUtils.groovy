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
* Setup the GPG Key to sign release artifacts
*/
def setupSigningKey(String gpgKeyCredentialsId) {
    withCredentials([file(credentialsId: gpgKeyCredentialsId, variable: 'SIGNING_KEY')]) {
        sh """#!/bin/bash -el
        gpg --list-keys
        gpg --batch --pinentry-mode=loopback --import $SIGNING_KEY
        """.trim()
    }
}

/**
* Sign an artifact using GPG
*/
def signArtifact(String artifactFileName) {
    sh """#!/bin/bash -el
    gpg --no-tty --batch --sign --pinentry-mode loopback --output ${artifactFileName}.asc --detach-sig ${artifactFileName}
    shasum -a 512 ${artifactFileName} > ${artifactFileName}.sha512
    """.trim()
}

/**
* Publish release artifacts to a SVN repository
*/
def publishArtifacts(String artifactsDir, String releaseRepository, String releaseVersion, String credentialsId) {
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'ASF_USERNAME', passwordVariable: 'ASF_PASSWORD')]) {
        sh """#!/bin/bash -el
        SVN_URL="${releaseRepository}/${releaseVersion}"
        # Check out the SVN working copy outside of the artifacts directory, so it is not copied into itself.
        SVN_WORKDIR=\$(mktemp -d)/svn-kie
        trap 'rm -rf "\$(dirname "\${SVN_WORKDIR}")"' EXIT

        # Create the RC directory in the SVN staging repository when it does not exist yet.
        # Only a "path does not exist" error triggers the creation; any other failure (network, auth, ...) aborts.
        if SVN_LS_OUTPUT=\$(svn ls --non-interactive "\${SVN_URL}" 2>&1); then
            echo "SVN directory \${SVN_URL} already exists"
        elif echo "\${SVN_LS_OUTPUT}" | grep -qE "E170000|160013|non-existent|doesn't exist"; then
            echo "SVN directory \${SVN_URL} does not exist, creating it"
            svn mkdir --parents --non-interactive --no-auth-cache --username \${ASF_USERNAME} --password "\${ASF_PASSWORD}" -m "Create Apache KIE ${releaseVersion} staging directory" "\${SVN_URL}"
        else
            echo "Unable to access \${SVN_URL}:"
            echo "\${SVN_LS_OUTPUT}"
            exit 1
        fi

        svn co --depth=empty "\${SVN_URL}" "\${SVN_WORKDIR}"
        find "${artifactsDir}" -maxdepth 1 -type f -exec cp {} "\${SVN_WORKDIR}" \\;
        cd "\${SVN_WORKDIR}"
        svn add . --force
        svn ci --non-interactive --no-auth-cache --username \${ASF_USERNAME} --password "\${ASF_PASSWORD}" -m "Apache KIE ${releaseVersion} artifacts"
        """.trim()
    }
}

/**
* Download release artifacts from a specific release
*/
def downloadReleaseArtifacts(String releaseRepository, String artifactsDir, String releaseVersion) {
    sh """#!/bin/bash -el
    mkdir -p "${artifactsDir}"
    svn co "${releaseRepository}/${releaseVersion}" "${artifactsDir}"
    """.trim()
}

/**
* Return a list of upstream images artifacts
*/
def getUpstreamImagesArtifactsList(String artifactsDir, String releaseVersion) {
    def components = [
        "kogito-base-builder",              // jenkinsfile.kogito-base-builder-image
        "kogito-data-index-ephemeral",      // jenkinsfile.kogito-data-index-ephemeral-image
        "kogito-data-index-postgresql",     // jenkinsfile.kogito-data-index-postgresql-image
        "kogito-jit-runner",                // jenkinsfile.kogito-jit-runner-image
        "kogito-jobs-service-allinone",     // jenkinsfile.kogito-jobs-service-allinone-image
        "kogito-jobs-service-ephemeral",    // jenkinsfile.kogito-jobs-service-ephemeral-image
        "kogito-jobs-service-postgresql"    // jenkinsfile.kogito-jobs-service-postgresql-image
    ]

    // clean -rc suffix if present
    def versionClean = releaseVersion.replaceAll(/(?i)-rc\d*/, '')

    return components.collect { component ->
        "${artifactsDir}/apache-kie-${versionClean}-incubating-${component}-image.tar.gz"
    }
}

return this
