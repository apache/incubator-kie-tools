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
def pushImageToRegistry(String registry, String account, String image, String tags, String userCredentialsId, String tokenCredentialsId) {
    withCredentials([string(credentialsId: userCredentialsId, variable: 'REGISTRY_USER')]) {
        withCredentials([string(credentialsId: tokenCredentialsId, variable: 'REGISTRY_TOKEN')]) {
            sh """
            echo "${REGISTRY_TOKEN}" | docker login -u "${REGISTRY_USER}" --password-stdin $registry
            """.trim()
            tagList = tags.split(' ')
            for (tag in tagList) {
                sh "docker push $registry/$account/$image:$tag"
            }
            sh 'docker logout'
        }
    }
}

/**
* @return bool image exists in a given registry
*/
def checkImageExistsInRegistry(String registry, String account, String image, String tag, String userCredentialsId, String tokenCredentialsId) {
    withCredentials([string(credentialsId: userCredentialsId, variable: 'DOCKER_USER')]) {
        withCredentials([string(credentialsId: tokenCredentialsId, variable: 'DOCKER_TOKEN')]) {
            sh """
            echo "${DOCKER_TOKEN}" | docker login -u "${DOCKER_USER}" --password-stdin $registry
            """.trim()
            result = sh returnStatus: true, script: """
            docker manifest inspect $registry/$account/$image:$tag > /dev/null
            """.trim()
            sh 'docker logout'
            return result == 0
        }
    }
}

/**
* Tag an image
*/
def tagImage(String registry, String image, String oldTag, String newTag) {
    sh "docker tag ${registry}/${image}:${oldTag} ${registry}/${image}:${newTag}"
}

/**
* Load an image
*/
def loadImage(String imageFile) {
    sh "docker load < ${imageFile}"
}

/**
* Load multiple images
*/
def loadImages(String... imagesFiles) {
    for (imageFile in imagesFiles) {
        loadImage(imageFile)
    }
}

return this
