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
* Upload an extension to Chrome Store
*
* @return String status
*/
def uploadExtension(String chromeStoreCredentialsId, String chromeStoreRefreshTokenCredentialsId, String file, String chromeExtensionIdCredentialsId) {
    withCredentials([usernamePassword(credentialsId: "${chromeStoreCredentialsId}", usernameVariable: 'CLIENT_ID', passwordVariable: 'CLIENT_SECRET')]) {
        withCredentials([string(credentialsId: "${chromeStoreRefreshTokenCredentialsId}", variable: 'REFRESH_TOKEN')]) {
            withCredentials([string(credentialsId: "${chromeExtensionIdCredentialsId}", variable: 'EXTENSION_ID')]) {
                accessToken = sh(returnStdout: true, script: "curl -X POST -fsS \"https://oauth2.googleapis.com/token\" -d \"client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&refresh_token=${REFRESH_TOKEN}&grant_type=refresh_token\" | jq -r '.access_token'").trim()
                uploadResponse = sh(returnStdout: true, script: "curl -X PUT -sS \"https://www.googleapis.com/upload/chromewebstore/v1.1/items/${EXTENSION_ID}\" -H \"Authorization: Bearer ${accessToken}\" -H \"x-goog-api-version:2\" -T ${file}").trim()

                return sh(returnStdout: true, script: "echo \"${uploadResponse}\" | jq -r '.uploadState'").trim()
            }
        }
    }
}

/**
* Publish an extension to Chrome Store
*
* @return String status
*/
def publishExtension(String chromeStoreCredentialsId, String chromeStoreRefreshTokenCredentialsId, String chromeExtensionIdCredentialsId) {
    withCredentials([usernamePassword(credentialsId: "${chromeStoreCredentialsId}", usernameVariable: 'CLIENT_ID', passwordVariable: 'CLIENT_SECRET')]) {
        withCredentials([string(credentialsId: "${chromeStoreRefreshTokenCredentialsId}", variable: 'REFRESH_TOKEN')]) {
            withCredentials([string(credentialsId: "${chromeExtensionIdCredentialsId}", variable: 'EXTENSION_ID')]) {
                script {
                    accessToken = sh(returnStdout: true, script: "curl -X POST -fsS \"https://oauth2.googleapis.com/token\" -d \"client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&refresh_token=${REFRESH_TOKEN}&grant_type=refresh_token\" | jq -r '.access_token'").trim()
                    publishResponse = sh(returnStdout: true, script: "curl -X POST -sS \"https://www.googleapis.com/chromewebstore/v1.1/items/${EXTENSION_ID}/publish\" -H \"Authorization: Bearer ${accessToken}\" -H \"x-goog-api-version:2\" -H \"Content-Length:\"").trim()

                    return sh(returnStdout: true, script: "echo \"${publishResponse}\" | jq -r '.status | .[0]'").trim()
                }
            }
        }
    }
}

return this
