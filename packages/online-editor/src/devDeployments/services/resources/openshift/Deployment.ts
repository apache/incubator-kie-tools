/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

export const createDeploymentYaml = `
kind: Deployment
apiVersion: apps/v1
metadata:
  name: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
  namespace: \${{ devDeployment.kubernetes.namespace }}
  labels:
    app: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    app.kubernetes.io/component: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    app.kubernetes.io/instance: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    app.kubernetes.io/name: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    app.kubernetes.io/part-of: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    \${{ devDeployment.labels.createdBy }}: kie-tools
  annotations:
    \${{ devDeployment.annotations.uri }}: \${{ devDeployment.workspace.resourceName }}
    \${{ devDeployment.annotations.workspaceId }}: \${{ devDeployment.workspace.id }}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
  template:
    metadata:
      labels:
        app: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
        deploymentconfig: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    spec:
      containers:
        - name: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
          image: \${{ devDeployment.defaultContainerImageUrl }}
          ports:
            - containerPort: 8080
              protocol: TCP
          env:
            - name: BASE_URL
              value: http://localhost/\${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
            - name: QUARKUS_PLATFORM_VERSION
              value: 2.16.7.Final
            - name: KOGITO_RUNTIME_VERSION
              value: 1.40.0.Final
            - name: ROOT_PATH
              value: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
          resources: {}
          imagePullPolicy: Always
`;

export const getDeploymentListApiPath = (namespace: string, labelSelector?: string) => {
  const selector = labelSelector ? `?labelSelector=${labelSelector}` : "";
  return `apis/app/v1/namespaces/${namespace}/deployments${selector}`;
};
