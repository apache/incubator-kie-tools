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
* Create or update an existing application in a Openshift cluster
*/
def createOrUpdateApp(String project, String appName, String imageTag, String imageUrl, String partOf, String deploymentIcon, String credentialsId, String deploymentEnvVarsPath='./deployment.env') {
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'OS_SERVER', passwordVariable: 'OS_TOKEN')]) {
        sh 'set +x && oc login --token=$OS_TOKEN --server=$OS_SERVER --insecure-skip-tls-verify'
        sh """#!/bin/bash -el
        oc project ${project}

        if [ ! -f ${deploymentEnvVarsPath} ]; then
          echo "ENV file does not exist"
          touch ${deploymentEnvVarsPath}
        fi

        if ! oc get deploy ${appName} > /dev/null 2>&1; then
          echo "Create the app '${appName}'"

          oc create imagestream ${appName}
          oc import-image ${appName}:${imageTag} --from=${imageUrl} --confirm
          oc tag ${appName}:${imageTag} ${appName}:latest

          oc label imagestreams/${appName} app=${appName}
          oc label imagestreams/${appName} app.kubernetes.io/component=${appName}
          oc label imagestreams/${appName} app.kubernetes.io/instance=${appName}
          oc label imagestreams/${appName} app.kubernetes.io/part-of=${partOf}

          oc new-app ${appName}:latest --name=${appName} --env-file=${deploymentEnvVarsPath}
          oc create route edge --service=${appName}

          oc label services/${appName} app.kubernetes.io/part-of=${partOf}
          oc label routes/${appName} app.kubernetes.io/part-of=${partOf}
          oc label deployments/${appName} app.kubernetes.io/part-of=${partOf}
          oc label deployments/${appName} app.openshift.io/runtime=${deploymentIcon}
        else
          echo "App '${appName}' already exists. Update the ImageStream instead."
          oc tag -d ${appName}:latest
          oc import-image ${appName}:${imageTag} --from=${imageUrl} --confirm
          oc tag ${appName}:${imageTag} ${appName}:latest
          cat ${deploymentEnvVarsPath} | oc set env deploy/${appName} -
        fi
        """.trim()
        sh 'oc logout'
    }
}

/**
* @return String route to the OpenShift application
*/
def getAppRoute(String project, String appName, String credentialsId) {
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'OS_SERVER', passwordVariable: 'OS_TOKEN')]) {
        sh 'set +x && oc login --token=$OS_TOKEN --server=$OS_SERVER --insecure-skip-tls-verify'
        sh "oc project ${project}"
        route = sh(returnStdout: true, script: "oc get route ${appName} -o jsonpath='{.spec.host}'").trim()
        sh 'oc logout'

        return "https://${route}"
    }
}

return this
