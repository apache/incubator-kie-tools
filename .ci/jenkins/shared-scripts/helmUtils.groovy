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
* Push a Helm Chart to a given registry
*/
def pushChartToRegistry(String registry, String chart, String credentialsId) {
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_PWD')]) {
        sh "set +x && helm registry login -u $REGISTRY_USER -p $REGISTRY_PWD $registry"
        sh "helm push ${chart} oci://${registry}"
        sh "helm registry logout ${registry}"
    }
}

return this;
