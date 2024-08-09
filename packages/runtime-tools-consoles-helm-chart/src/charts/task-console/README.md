<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# task-console

![Version: 0.0.0](https://img.shields.io/badge/Version-0.0.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 0.0.0](https://img.shields.io/badge/AppVersion-0.0.0-informational?style=flat-square)

A Helm chart to deploy Runtime Tools Task Console on Kubernetes

## Values

| Key                             | Type   | Default                                                                                                                                                                                                                                           | Description                                                                                                                                |
| ------------------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| appNameOverride                 | string | `""`                                                                                                                                                                                                                                              | Overrides the deployed application name                                                                                                    |
| appTaskActiveStatesListOverride | string | `""`                                                                                                                                                                                                                                              | Overrides the Task Console active states list                                                                                              |
| appTaskStatesListOverride       | string | `""`                                                                                                                                                                                                                                              | Overrides the Task Console states list                                                                                                     |
| appVersionOverride              | string | `""`                                                                                                                                                                                                                                              | Overrides the deployed application version                                                                                                 |
| autoscaling                     | object | `{"enabled":false,"maxReplicas":100,"minReplicas":1,"targetCPUUtilizationPercentage":80}`                                                                                                                                                         | Task Console HorizontalPodAutoscaler configuration (https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)            |
| enabled                         | bool   | `true`                                                                                                                                                                                                                                            | Enable or disable Task Console installation                                                                                                |
| fullnameOverride                | string | `""`                                                                                                                                                                                                                                              | Overrides charts full name                                                                                                                 |
| image                           | object | `{"account":"apache","name":"incubator-kie-kogito-task-console","pullPolicy":"IfNotPresent","registry":"docker.io","tag":"main"}`                                                                                                                 | Image source configuration for the Management Console image                                                                                |
| imagePullSecrets                | list   | `[]`                                                                                                                                                                                                                                              | Pull secrets used when pulling Management Console image                                                                                    |
| ingress                         | object | `{"annotations":{},"className":"{{ .Values.global.kubernetesIngressClass }}","enabled":false,"hosts":[{"host":"task-console.{{ .Values.global.kubernetesClusterDomain }}","paths":[{"path":"/","pathType":"ImplementationSpecific"}]}],"tls":[]}` | Task Console Ingress configuration (https://kubernetes.io/docs/concepts/services-networking/ingress/)                                      |
| name                            | string | `"task-console"`                                                                                                                                                                                                                                  | Component name                                                                                                                             |
| nameOverride                    | string | `""`                                                                                                                                                                                                                                              | Overrides charts name                                                                                                                      |
| openshiftRoute                  | object | `{"annotations":{},"enabled":false,"host":"task-console.{{ .Values.global.openshiftRouteDomain }}","tls":{"insecureEdgeTerminationPolicy":"None","termination":"edge"}}`                                                                          | Task Console OpenShift Route configuration (https://docs.openshift.com/container-platform/4.14/networking/routes/route-configuration.html) |
| service                         | object | `{"nodePort":"","port":8080,"targetPort":8080,"type":"ClusterIP"}`                                                                                                                                                                                | Task Console Service configuration (https://kubernetes.io/docs/concepts/services-networking/service/)                                      |
| serviceAccount                  | object | `{"annotations":{},"create":true,"name":""}`                                                                                                                                                                                                      | Task Console ServiceAccount configuration (https://kubernetes.io/docs/concepts/security/service-accounts/)                                 |

---

Autogenerated from chart metadata using [helm-docs v1.13.1](https://github.com/norwoodj/helm-docs/releases/v1.13.1)
