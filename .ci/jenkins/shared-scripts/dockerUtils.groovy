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
* Push an image to a given registry
*/
def pushImageToRegistry(String registry, String image, String tags, String credentialsId) {
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_PWD')]) {
        sh "set +x && docker login -u $REGISTRY_USER -p $REGISTRY_PWD $registry"
        tagList = tags.split(' ')
        for (tag in tagList) {
            sh "docker push $registry/$image:$tag"
        }
        sh 'docker logout'
    }
}

/**
* @return bool image exists in a given registry
*/
def checkImageExistsInRegistry(String registry, String image, String tag, String credentialsId) {
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_PWD')]) {
        sh "set +x && docker login -u $REGISTRY_USER -p $REGISTRY_PWD $registry"
        result = sh returnStatus: true, script: """
        docker manifest inspect $registry/$image:$tag > /dev/null 2>&1
        """.trim()
        sh 'docker logout'
        return result == 0
    }
}

return this;
