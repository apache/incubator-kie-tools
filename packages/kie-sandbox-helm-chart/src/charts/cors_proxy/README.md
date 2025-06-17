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

# cors_proxy

![Version: 10.1.0](https://img.shields.io/badge/Version-10.1.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 10.1.0](https://img.shields.io/badge/AppVersion-10.1.0-informational?style=flat-square)

A Helm chart to deploy CORS Proxy on Kubernetes

## Values

| Key              | Type   | Default                                                                                                                                                                                                                                         | Description                                                                                                                              |
| ---------------- | ------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| autoscaling      | object | `{"enabled":false,"maxReplicas":100,"minReplicas":1,"targetCPUUtilizationPercentage":80}`                                                                                                                                                       | CORS Proxy HorizontalPodAutoscaler configuration (https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)            |
| fullnameOverride | string | `""`                                                                                                                                                                                                                                            | Overrides charts full name                                                                                                               |
| image            | object | `{"account":"apache","name":"incubator-kie-cors-proxy","pullPolicy":"IfNotPresent","registry":"docker.io","tag":"10.1.0"}`                                                                                                                      | Image source configuration for the CORS Proxy image                                                                                      |
| imagePullSecrets | list   | `[]`                                                                                                                                                                                                                                            | Pull secrets used when pulling CORS Proxy image                                                                                          |
| ingress          | object | `{"annotations":{},"className":"{{ .Values.global.kubernetesIngressClass }}","enabled":false,"hosts":[{"host":"cors-proxy.{{ .Values.global.kubernetesClusterDomain }}","paths":[{"path":"/","pathType":"ImplementationSpecific"}]}],"tls":[]}` | CORS Proxy Ingress configuration (https://kubernetes.io/docs/concepts/services-networking/ingress/)                                      |
| name             | string | `"cors-proxy"`                                                                                                                                                                                                                                  | The CORS Proxy application name                                                                                                          |
| nameOverride     | string | `""`                                                                                                                                                                                                                                            | Overrides charts name                                                                                                                    |
| nodeSelector     | object | `{}`                                                                                                                                                                                                                                            |                                                                                                                                          |
| openshiftRoute   | object | `{"annotations":{},"enabled":false,"host":"cors-proxy.{{ .Values.global.openshiftRouteDomain }}","tls":{"insecureEdgeTerminationPolicy":"None","termination":"edge"}}`                                                                          | CORS Proxy OpenShift Route configuration (https://docs.openshift.com/container-platform/4.14/networking/routes/route-configuration.html) |
| service          | object | `{"nodePort":"","port":8080,"type":"ClusterIP"}`                                                                                                                                                                                                | CORS Proxy Service configuration (https://kubernetes.io/docs/concepts/services-networking/service/)                                      |
| serviceAccount   | object | `{"annotations":{},"create":true,"name":""}`                                                                                                                                                                                                    | CORS Proxy ServiceAccount configuration (https://kubernetes.io/docs/concepts/security/service-accounts/)                                 |

---

Autogenerated from chart metadata using [helm-docs v1.13.1](https://github.com/norwoodj/helm-docs/releases/v1.13.1)
