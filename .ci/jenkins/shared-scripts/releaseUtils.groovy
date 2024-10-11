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
        svn co --depth=empty ${releaseRepository}/${releaseVersion} svn-kie
        cp ${artifactsDir}/* svn-kie
        cd svn-kie
        svn add . --force
        svn ci --non-interactive --no-auth-cache --username ${ASF_USERNAME} --password '${ASF_PASSWORD}' -m "Apache KIE ${releaseVersion} artifacts"
        rm -rf svn-kie
        """.trim()
    }
}

/**
* Download release artifacts from a specific release
*/
def downloadReleaseArtifacts(String releaseRepository, String releaseVersion, String artifactsDir, String... artifactsNames) {
    sh """#!/bin/bash -el
    mkdir -p "${artifactsDir}" || true"
    """.trim()
    for (artifactName in artifactsNames) {
        sh """#!/bin/bash -el
        svn cat "${releaseRepository}/${releaseVersion}/${artifactName}" > "${artifactsDir}/${artifactName}"
        svn cat "${releaseRepository}/${releaseVersion}/${artifactName}.asc" > "${artifactsDir}/${artifactName}.asc"
        svn cat "${releaseRepository}/${releaseVersion}/${artifactName}.sha512" > "${artifactsDir}/${artifactName}.sha512"
        """.trim()
    }
}

/**
* Return a list of upstream images artifacts
*/
def getUpstreamImagesArtifactsList(String releaseVersion) {
    return [
        "apache-kie-${releaseVersion}-incubating-kogito-base-builder-image.tar.gz",
        "apache-kie-${releaseVersion}-incubating-kogito-data-index-ephemeral-image.tar.gz",
        "apache-kie-${releaseVersion}-incubating-kogito-data-index-postgresql-image.tar.gz",
        "apache-kie-${releaseVersion}-incubating-kogito-jit-runner-image.tar.gz",
        "apache-kie-${releaseVersion}-incubating-kogito-jobs-service-allinone-image.tar.gz",
        "apache-kie-${releaseVersion}-incubating-kogito-jobs-service-ephemeral-image.tar.gz",
        "apache-kie-${releaseVersion}-incubating-kogito-jobs-service-postgresql-image.tar.gz"
    ].toArray()
}

/**
* Rename a release candidate artifact name to the final release name
**/
def renameArtifactsToFinalVersion(String releaseCandidateArtifactsDir, String releaseCandidateVersion, String releaseVersion, String releaseArtifactsDir, String... releaseCandidateArtifactsNames) {
    for (releaseCandidateArtifactName in releaseCandidateArtifactsNames) {
        finalArtifactName = releaseCandidateArtifactName.replace(releaseCandidateVersion, releaseVersion)
        sh """#!/bin/bash -el
        mv ${releaseCandidateArtifactsDir}/${releaseCandidateArtifactName} ${releaseArtifactsDir}/${finalArtifactName}
        mv ${releaseCandidateArtifactsDir}/${releaseCandidateArtifactName}.asc ${releaseArtifactsDir}/${finalArtifactName}.asc
        mv ${releaseCandidateArtifactsDir}/${releaseCandidateArtifactName}.sha512 ${releaseArtifactsDir}/${finalArtifactName}.sha512
        sed -i 's/${releaseCandidateVersion}/${releaseVersion}/g' ${releaseArtifactsDir}/${finalArtifactName}.sha512
        """.trim()
    }
}

/**
* Copy legal files to a specific directory
**/
def copyLegalFiles(String targetDir) {
    sh """#!/bin/bash -el
    mkdir -p "${targetDir}" || true"
    cp {LICENSE,NOTICE,DISCLAIMER-WIP} ${targetDir}
    """.trim()
}

/**
* Add Apache legal files to a .tar.gz file
**/
def addLegalfilesToTarGzFile(String artifactsDir, String tarGzFile, String legalFilesDir) {
    tarFile = tarGzFile.replace('.gz', '')
    sh """#!/bin/bash -el
    cd ${legalFilesDir}
    zcat ${artifactsDir}/${tarGzFile} | dd of=${tarFile} bs=512 skip=1
    tar -rvf ${tarFile} ./*.txt
    gzip -q -c ${tarFile} > ${artifactsDir}/${tarGzFile}
    """.trim()
}

return this
