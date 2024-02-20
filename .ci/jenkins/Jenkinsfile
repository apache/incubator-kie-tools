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
@Library('jenkins-pipeline-shared-libraries')_

pipeline {
    agent {
        label util.avoidFaultyNodes('ubuntu')
    }
    options {
        timeout(time: 240, unit: 'MINUTES')
        disableConcurrentBuilds(abortPrevious: true)
        skipDefaultCheckout()
    }
    stages {
        stage("Invoke build-and-test") {
            steps {
                script {
                    sh 'printenv'
                    List buildParams = []
                    buildParams.add(string(name: 'DISPLAY_NAME', value: "PR #${CHANGE_ID}: ${CHANGE_URL}"))
                    buildParams.add(string(name: 'CHANGE_ID', value: CHANGE_ID))
                    buildParams.add(string(name: 'CHANGE_URL', value: CHANGE_URL))
                    buildParams.add(string(name: 'SOURCE_REPOSITORY', value: getChangeRepository()))
                    buildParams.add(string(name: 'SOURCE_AUTHOR', value: getChangeAuthor()))
                    buildParams.add(string(name: 'SOURCE_BRANCH', value: getChangeBranch()))
                    buildParams.add(string(name: 'TARGET_BRANCH', value: getChangeTarget()))
                    buildParams.add(string(name: 'BUILD_KOGITO_APPS_REF', value: getChangeTarget()))
                    // Keep executing so we can cancel all if needed
                    def job = build(job: "../kogito-images.build-and-test", wait: true, parameters: buildParams, propagate: false)
                    if (job.result != 'SUCCESS') {
                        if (job.result == 'UNSTABLE') {
                            unstable("Tests on images seems to have failed: ${job.absoluteUrl}")
                        } else {
                            error("Error building images. Please check the logs of the job: ${job.absoluteUrl}")
                        }
                    }
                }
            }
        }
    }
}

String getChangeAuthor() {
    return pullrequest.getAuthorAndRepoForPr().split('/')[0]
}

String getChangeRepository() {
    return pullrequest.getAuthorAndRepoForPr().split('/')[1]
}

String getChangeBranch() {
    return CHANGE_BRANCH
}

String getChangeTarget() {
    return CHANGE_TARGET
}

String getGitAuthorCredentialsId() {
    return env.AUTHOR_CREDS_ID
}