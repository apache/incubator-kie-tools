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

export function FormWebappServiceYaml() {
  return `
kind: Service
apiVersion: v1
metadata:
  name: \${{ devDeployment.uniqueName }}-dmn-form-webapp
  namespace: \${{ devDeployment.kubernetes.namespace }}
  labels:
    app: \${{ devDeployment.uniqueName }}
    app.kubernetes.io/component: \${{ devDeployment.uniqueName }}-dmn-form-webapp
    app.kubernetes.io/instance: \${{ devDeployment.uniqueName }}-dmn-form-webapp
    app.kubernetes.io/name: \${{ devDeployment.uniqueName }}-dmn-form-webapp
    app.kubernetes.io/part-of: \${{ devDeployment.uniqueName }}
spec:
  ports:
    - protocol: TCP
      port: 8081
      targetPort: 8081
  selector:
    app: \${{ devDeployment.uniqueName }}
    deploymentconfig: \${{ devDeployment.uniqueName }}
  type: ClusterIP
`;
}
