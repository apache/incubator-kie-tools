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

# runtime-tools-consoles-helm-chart

This chart can be used to deploy the Management Console image on a [Kubernetes](https://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Additional requirements

- Docker
- Minikube

## Components

- Management Console

## Installing the Chart

### Default install

To install the chart with the release name `runtime-tools-consoles`:

```console
$ helm install runtime-tools-consoles ./src"
```

Following message should be displayed on your console.

```console
NAME: runtime-tools-consoles
LAST DEPLOYED: Wed Aug 7 17:09:04 2024
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
In order to get runtime-tools-consoles running you need to run these commands:

1. Run the following commands in a separate terminal to port-forward Management Console application:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/component=management-console,app.kubernetes.io/instance=runtime-tools-consoles" -o jsonpath="{.items[0].metadata.name}")
  export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
  echo "Management Console URL: http://127.0.0.1:8081"
  kubectl --namespace default port-forward $POD_NAME 8081:$CONTAINER_PORT
```

### Minikube install

To install the chart with the release name `runtime-tools-consoles`:

```console
$ helm install runtime-tools-consoles ./src --values ./src/values-minikube-nginx.yaml"
```

Following message should be displayed on your console.

```console
NAME: runtime-tools-consoles
LAST DEPLOYED: Wed Aug 7 17:09:04 2024
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
You may need to add the above hostnames to your /etc/hosts file, mapping them to your minikube ip.

Run the following commands:
  export MINIKUBE_IP=$(minikube ip)
  echo "\n# Minikube Runtime Tools Consoles Helm Chart hostnames" | sudo tee -a /etc/hosts
  echo "$MINIKUBE_IP management-console.local" | sudo tee -a /etc/hosts
```

### Kubernetes install

To install the chart with the release name `runtime-tools-consoles`:

```console
$ helm install runtime-tools-consoles ./src --values ./src/values-kubernetes.yaml --set global.kubernetesClusterDomain="<YOUR_KUBERNETES_CLUSTER_DOMAIN>" --set global.kubernetesIngressClass="<YOUR_KUBERNETES_INGRESS_CLASS>"
```

Following message should be displayed on your console.

```console
NAME: runtime-tools-consoles
LAST DEPLOYED: Wed Aug 7 17:09:04 2024
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
1. Management Console available at:
  http://management-console.<YOUR_KUBERNETES_CLUSTER_DOMAIN>
```

### OpenShift install

First, you may need to get the default OpenShift domain for your routes with this command:

```console
$ oc get ingresses.config cluster --output jsonpath={.spec.domain}
```

If you don't have access rigths to this config, try creating a dummy Route resource and checking its domain.

To install the chart with the release name `runtime-tools-consoles`:

```console
$ helm install runtime-tools-consoles ./src --values ./src/values-openshift.yaml --set global.openshiftRouteDomain="<YOUR_OCP_ROUTE_DOMAIN>"
```

Following message should be displayed on your console.

```console
NAME: runtime-tools-consoles
LAST DEPLOYED: Wed Aug 7 17:09:04 2024
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
1. Management Console available at:
  https://management-console.<YOUR_OCP_ROUTE_DOMAIN>
```

## Installing a released version from the OCI registry:

Very similar to the way you install the chart from source code, you can also install a released version available on docker.io registry:

### Default install

```console
$ helm install runtime-tools-consoles oci://docker.io/apache/incubator-kie-runtime-tools-consoles-helm-chart --version=0.0.0-main
```

### Minikube install

```console
$ helm pull oci://docker.io/apache/incubator-kie-runtime-tools-consoles-helm-chart --version=0.0.0-main --untar
$ helm install runtime-tools-consoles ./incubator-kie-runtime-tools-consoles-helm-chart --values ./incubator-kie-runtime-tools-consoles-helm-chart/values-minikube-nginx.yaml
```

### Kubernetes install

```console
$ helm pull oci://docker.io/apache/incubator-kie-runtime-tools-consoles-helm-chart --version=0.0.0-main --untar
$ helm install runtime-tools-consoles ./incubator-kie-runtime-tools-consoles-helm-chart --values ./incubator-kie-runtime-tools-consoles-helm-chart/values-kubernetes.yaml --set global.kubernetesClusterDomain="<YOUR_KUBERNETES_CLUSTER_DOMAIN>" --set global.kubernetesIngressClass="<YOUR_KUBERNETES_INGRESS_CLASS>"
```

### OpenShift install

```console
$ helm pull oci://docker.io/apache/incubator-kie--tools-consoles-helm-chart --version=0.0.0-main --untar
$ helm install runtime-tools-consoles ./incubator-kie-runtime-tools-consoles-helm-chart --values ./incubator-kie-runtime-tools-consoles-helm-chart/values-openshift.yaml --set global.openshiftRouteDomain="<YOUR_OCP_ROUTE_DOMAIN>"
```

## Uninstalling the Chart

To uninstall the `runtime-tools-consoles` deployment:

```console
$ helm uninstall runtime-tools-consoles
```

## Passing Environmental variables

This chart uses default environmental variables from `values.yaml` file. We can override those by passing it from command line.

```console
$ helm install runtime-tools-consoles ./src --set image.repository=docker.io
```

## Configuration

The following table lists the configurable parameters of the Runtime Tools Consoles helm chart and their default values.

<!-- CHART_VALUES_README -->

| Key                                     | Type   | Default                                                                                                                                                                                                                                                 | Description                                                                                                                                      |
| --------------------------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| global.ingressSource                    | string | `""`                                                                                                                                                                                                                                                    | Which ingress source is being used (none/"minikube"/"kubernetes"/"openshift") Obs.: For NOTES generation only                                    |
| global.kubernetesClusterDomain          | string | `""`                                                                                                                                                                                                                                                    | If using Minikube or Kubernetes, set the cluster domain                                                                                          |
| global.kubernetesIngressClass           | string | `""`                                                                                                                                                                                                                                                    | If using Minikube or Kubernetes, set the Ingress class (i.e: nginx)                                                                              |
| global.openshiftRouteDomain             | string | `""`                                                                                                                                                                                                                                                    | If using OpenShift Routes, set the Route domain                                                                                                  |
| fullnameOverride                        | string | `""`                                                                                                                                                                                                                                                    | Overrides charts full name                                                                                                                       |
| nameOverride                            | string | `""`                                                                                                                                                                                                                                                    | Overrides charts name                                                                                                                            |
| management-console.appNameOverride      | string | `""`                                                                                                                                                                                                                                                    | Overrides the deployed application name                                                                                                          |
| management-console.autoscaling          | object | `{"enabled":false,"maxReplicas":100,"minReplicas":1,"targetCPUUtilizationPercentage":80}`                                                                                                                                                               | Management Console HorizontalPodAutoscaler configuration (https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)            |
| management-console.enabled              | bool   | `true`                                                                                                                                                                                                                                                  | Enable or disable Management Console installation                                                                                                |
| management-console.fullnameOverride     | string | `""`                                                                                                                                                                                                                                                    | Overrides charts full name                                                                                                                       |
| management-console.image                | object | `{"account":"apache","name":"incubator-kie-kogito-management-console","pullPolicy":"IfNotPresent","registry":"docker.io","tag":"main"}`                                                                                                                 | Image source configuration for the Management Console image                                                                                      |
| management-console.imagePullSecrets     | list   | `[]`                                                                                                                                                                                                                                                    | Pull secrets used when pulling Management Console image                                                                                          |
| management-console.ingress              | object | `{"annotations":{},"className":"{{ .Values.global.kubernetesIngressClass }}","enabled":false,"hosts":[{"host":"management-console.{{ .Values.global.kubernetesClusterDomain }}","paths":[{"path":"/","pathType":"ImplementationSpecific"}]}],"tls":[]}` | Management Console Ingress configuration (https://kubernetes.io/docs/concepts/services-networking/ingress/)                                      |
| management-console.name                 | string | `"management-console"`                                                                                                                                                                                                                                  | Component name                                                                                                                                   |
| management-console.nameOverride         | string | `""`                                                                                                                                                                                                                                                    | Overrides charts name                                                                                                                            |
| management-console.nodeSelector         | object | `{}`                                                                                                                                                                                                                                                    |                                                                                                                                                  |
| management-console.oidcClientIdOverride | string | `""`                                                                                                                                                                                                                                                    | Overrides the OIDC Client ID used by the Management Console                                                                                      |
| management-console.openshiftRoute       | object | `{"annotations":{},"enabled":false,"host":"management-console.{{ .Values.global.openshiftRouteDomain }}","tls":{"insecureEdgeTerminationPolicy":"None","termination":"edge"}}`                                                                          | Management Console OpenShift Route configuration (https://docs.openshift.com/container-platform/4.14/networking/routes/route-configuration.html) |
| management-console.service              | object | `{"nodePort":"","port":8081,"targetPort":8080,"type":"ClusterIP"}`                                                                                                                                                                                      | Management Console Service configuration (https://kubernetes.io/docs/concepts/services-networking/service/)                                      |
| management-console.serviceAccount       | object | `{"annotations":{},"create":true,"name":""}`                                                                                                                                                                                                            | Management Console ServiceAccount configuration (https://kubernetes.io/docs/concepts/security/service-accounts/)                                 |

---

Autogenerated from chart metadata using [helm-docs v1.13.1](https://github.com/norwoodj/helm-docs/releases/v1.13.1)
