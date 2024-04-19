/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
openshiftApiKey = ''
openshiftApiCredsKey = ''

openshiftInternalRegistry = 'image-registry.openshift-image-registry.svc:5000'

void loginOpenshift() {
    withCredentials([string(credentialsId: openshiftApiKey, variable: 'OPENSHIFT_API')]) {
        withCredentials([usernamePassword(credentialsId: openshiftApiCredsKey, usernameVariable: 'OC_USER', passwordVariable: 'OC_PWD')]) {
            sh "oc login --username=${OC_USER} --password=${OC_PWD} --server=${OPENSHIFT_API} --insecure-skip-tls-verify"
        }
    }
}

String getOpenshiftRegistry() {
    return sh(returnStdout: true, script: 'oc get routes -n openshift-image-registry | tail -1 | awk \'{print $2}\'').trim()
}

return this
