# KIE Sandbox Helm Chart

This chart can be used to deploy KIE Sandbox image on a [Kubernetes](https://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Additional requirements

- Podman (for Linux)
- Docker (for macOS)
- Minikube

## Components

- KIE Sandbox: main application
- Extended Services: powers the DMN Runner and validator
- CORS Proxy: intended to be used to solve CORS issues

## Installing the Chart

### Default install

To install the chart with the release name `kie-sandbox`:

```console
$ helm install kie-sandbox ./src
```

Following message should be displayed on your console.

```console
NAME: kie-sandbox
LAST DEPLOYED: Wed Nov 29 17:09:04 2023
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
In order to get KIE sandbox running you need to run these commands:

1. Run the following commands in a separate terminal to port-forward CORS Proxy component:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kie_sandbox,app.kubernetes.io/component=cors-proxy,app.kubernetes.io/instance=kie-sandbox" -o jsonpath="{.items[0].metadata.name}")
  export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
  echo "CORS Proxy URL: http://127.0.0.1:8081"
  kubectl --namespace default port-forward $POD_NAME 8081:$CONTAINER_PORT

2. Run the following commands in a separate terminal to port-forward Extendend Services component:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kie_sandbox,app.kubernetes.io/component=extended-services,app.kubernetes.io/instance=kie-sandbox" -o jsonpath="{.items[0].metadata.name}")
  export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
  echo "Extended Services URL: http://127.0.0.1:21345"
  kubectl --namespace default port-forward $POD_NAME 21345:$CONTAINER_PORT

3. Run the following commands in a separate terminal to port-forward Sanxbox component and get the application URL:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kie_sandbox,app.kubernetes.io/component=kie-sandbox,app.kubernetes.io/instance=kie-sandbox" -o jsonpath="{.items[0].metadata.name}")
  export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
  echo "KIE Sandbox URL http://127.0.0.1:8080"
  kubectl --namespace default port-forward $POD_NAME 8080:$CONTAINER_PORT
```

Run above commands to forward container ports to your system ports. After this, KIE Sandbox should be accessible via http://127.0.0.1:8080

### Minikube install

To install the chart with the release name `kie-sandbox`:

```console
$ helm install kie-sandbox ./src --values ./src/values-minikube-nginx.yaml
```

Following message should be displayed on your console.

```console
NAME: kie-sandbox
LAST DEPLOYED: Wed Nov 29 17:09:04 2023
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
You may need to add the above hostnames to your /etc/hosts file, mapping them to your minikube ip.

Run the following commands:
  export MINIKUBE_IP=$(minikube ip)
  echo "\n# Minikube KIE Sandbox Helm Chart hostnames" | sudo tee -a /etc/hosts
  echo "$MINIKUBE_IP cors-proxy.local" | sudo tee -a /etc/hosts
  echo "$MINIKUBE_IP extended-services.local" | sudo tee -a /etc/hosts
  echo "$MINIKUBE_IP kie-sandbox.local" | sudo tee -a /etc/hosts
```

### Kubernetes install

To install the chart with the release name `kie-sandbox`:

```console
$ helm install kie-sandbox ./src --values ./src/values-kubernetes.yaml --set global.kubernetesClusterDomain="<YOUR_KUBERNETES_CLUSTER_DOMAIN>" --set global.kubernetesIngressClass="<YOUR_KUBERNETES_INGRESS_CLASS>"
```

Following message should be displayed on your console.

```console
NAME: kie-sandbox
LAST DEPLOYED: Wed Nov 29 17:09:04 2023
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
1. CORS Proxy available at:
  http://cors-proxy.<YOUR_KUBERNETES_CLUSTER_DOMAIN>
2. Extended Services available at:
  http://extended-services.<YOUR_KUBERNETES_CLUSTER_DOMAIN>
3. KIE Sandbox available at:
  http://kie-sandbox.<YOUR_KUBERNETES_CLUSTER_DOMAIN>
```

No need to run any commands. KIE Sandbox should be accessible via https://kie-sandbox.<YOUR_KUBERNETES_CLUSTER_DOMAIN>

### OpenShift install

First, you may need to get the default OpenShift domain for your routes with this command:

```console
$ oc get ingresses.config cluster --output jsonpath={.spec.domain}
```

If you don't have access rigths to this config, try creating a dummy Route resource and checking its domain.

To install the chart with the release name `kie-sandbox`:

```console
$ helm install kie-sandbox ./src --values ./src/values-openshift.yaml --set global.openshiftRouteDomain="<YOUR_OCP_ROUTE_DOMAIN>"
```

Following message should be displayed on your console.

```console
NAME: kie-sandbox
LAST DEPLOYED: Wed Nov 29 17:09:04 2023
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
1. CORS Proxy available at:
  https://cors-proxy.<YOUR_OCP_ROUTE_DOMAIN>
2. Extended Services available at:
  https://extended-services.<YOUR_OCP_ROUTE_DOMAIN>
3. KIE Sandbox available at:
  https://kie-sandbox.<YOUR_OCP_ROUTE_DOMAIN>
```

No need to run any commands. KIE Sandbox should be accessible via https://kie-sandbox.<YOUR_OCP_ROUTE_DOMAIN>

## Installing a released version from the OCI registry:

Very similar to the way you install the chart from source code, you can also install a released version available on quay.io registry:

### Default install

```console
$ helm install kie-sandbox oci://quay.io/kie-tools/kie-sandbox-helm-chart --version=0.0.0
```

### Minikube install

```console
$ helm pull oci://quay.io/kie-tools/kie-sandbox-helm-chart --version=0.0.0 --untar
$ helm install kie-sandbox ./kie-sandbox-helm-chart --values ./kie-sandbox-helm-chart/values-minikube-nginx.yaml
```

### Kubernetes install

```console
$ helm pull oci://quay.io/kie-tools/kie-sandbox-helm-chart --version=0.0.0 --untar
$ helm install kie-sandbox ./kie-sandbox-helm-chart --values ./kie-sandbox-helm-chart/values-kubernetes.yaml --set global.kubernetesClusterDomain="<YOUR_KUBERNETES_CLUSTER_DOMAIN>" --set global.kubernetesIngressClass="<YOUR_KUBERNETES_INGRESS_CLASS>"
```

### OpenShift install

```console
$ helm pull oci://quay.io/kie-tools/kie-sandbox-helm-chart --version=0.0.0 --untar
$ helm install kie-sandbox ./kie-sandbox-helm-chart --values ./kie-sandbox-helm-chart/values-openshift.yaml --set global.openshiftRouteDomain="<YOUR_OCP_ROUTE_DOMAIN>"
```

## Uninstalling the Chart

To uninstall the `kie-sandbox` deployment:

```console
$ helm uninstall kie-sandbox
```

## Passing Environmental variables

This chart uses default environmental variables from `values.yaml` file. We can override those by passing it from command line.

```console
$ helm install kie-sandbox ./src --set image.repository=quay.io
```

## Configuration

The following table lists the configurable parameters of the KIE Sandbox chart and their default values.

<details>
  <summary markdown="span">How to update this list?</summary>
  <ul>
    <li>Install <a href="https://github.com/norwoodj/helm-docs">norwoodj/helm-docs</a>;</li>
    <li>
      Run the following commands:
<pre>
$ cd src
$ helm-docs --document-dependency-values=true --chart-search-root=.
</pre>
    </li>
    <li>
      Run the install script.
    </li>
  </ul>
</details>

<!-- CHART_VALUES_README -->

| Key                                | Type   | Default                                                                                                                                                                                                                                                | Description                                                                                                                                     |
| ---------------------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| global.ingressSource               | string | `""`                                                                                                                                                                                                                                                   | Which ingress source is being used (none/"minikube"/"kubernetes"/"openshift") Obs.: For NOTES generation only                                   |
| global.kubernetesClusterDomain     | string | `""`                                                                                                                                                                                                                                                   | If using Minikube or Kubernetes, set the cluster domain                                                                                         |
| global.kubernetesIngressClass      | string | `""`                                                                                                                                                                                                                                                   | If using Minikube or Kubernetes, set the Ingress class (i.e: nginx)                                                                             |
| global.openshiftRouteDomain        | string | `""`                                                                                                                                                                                                                                                   | If using OpenShift Routes, set the Route domain                                                                                                 |
| fullnameOverride                   | string | `""`                                                                                                                                                                                                                                                   | Overrides charts full name                                                                                                                      |
| nameOverride                       | string | `""`                                                                                                                                                                                                                                                   | Overrides charts name                                                                                                                           |
| cors_proxy.autoscaling             | object | `{"enabled":false,"maxReplicas":100,"minReplicas":1,"targetCPUUtilizationPercentage":80}`                                                                                                                                                              | CORS Proxy HorizontalPodAutoscaler configuration (https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)                   |
| cors_proxy.fullnameOverride        | string | `""`                                                                                                                                                                                                                                                   | Overrides charts full name                                                                                                                      |
| cors_proxy.image                   | object | `{"account":"kie-tools","name":"cors-proxy-image","pullPolicy":"IfNotPresent","registry":"quay.io","tag":"latest"}`                                                                                                                                    | Image source configuration for the CORS Proxy image                                                                                             |
| cors_proxy.imagePullSecrets        | list   | `[]`                                                                                                                                                                                                                                                   | Pull secrets used when pulling CORS Proxy image                                                                                                 |
| cors_proxy.ingress                 | object | `{"annotations":{},"className":"{{ .Values.global.kubernetesIngressClass }}","enabled":false,"hosts":[{"host":"cors-proxy.{{ .Values.global.kubernetesClusterDomain }}","paths":[{"path":"/","pathType":"ImplementationSpecific"}]}],"tls":[]}`        | CORS Proxy Ingress configuration (https://kubernetes.io/docs/concepts/services-networking/ingress/)                                             |
| cors_proxy.name                    | string | `"cors-proxy"`                                                                                                                                                                                                                                         | The CORS Proxy application name                                                                                                                 |
| cors_proxy.nameOverride            | string | `""`                                                                                                                                                                                                                                                   | Overrides charts name                                                                                                                           |
| cors_proxy.openshiftRoute          | object | `{"annotations":{},"enabled":false,"host":"cors-proxy.{{ .Values.global.openshiftRouteDomain }}","tls":{"insecureEdgeTerminationPolicy":"None","termination":"edge"}}`                                                                                 | CORS Proxy OpenShift Route configuration (https://docs.openshift.com/container-platform/4.14/networking/routes/route-configuration.html)        |
| cors_proxy.service                 | object | `{"nodePort":"","port":8080,"type":"ClusterIP"}`                                                                                                                                                                                                       | CORS Proxy Service configuration (https://kubernetes.io/docs/concepts/services-networking/service/)                                             |
| cors_proxy.serviceAccount          | object | `{"annotations":{},"create":true,"name":""}`                                                                                                                                                                                                           | CORS Proxy ServiceAccount configuration (https://kubernetes.io/docs/concepts/security/service-accounts/)                                        |
| extended_services.autoscaling      | object | `{"enabled":false,"maxReplicas":100,"minReplicas":1,"targetCPUUtilizationPercentage":80}`                                                                                                                                                              | Extended Services HorizontalPodAutoscaler configuration (https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)            |
| extended_services.fullnameOverride | string | `""`                                                                                                                                                                                                                                                   | Overrides charts full name                                                                                                                      |
| extended_services.image            | object | `{"account":"kie-tools","name":"kie-sandbox-extended-services-image","pullPolicy":"IfNotPresent","registry":"quay.io","tag":"latest"}`                                                                                                                 | Image source configuration for the Extended Services image                                                                                      |
| extended_services.imagePullSecrets | list   | `[]`                                                                                                                                                                                                                                                   | Pull secrets used when pulling Extended Services image                                                                                          |
| extended_services.ingress          | object | `{"annotations":{},"className":"{{ .Values.global.kubernetesIngressClass }}","enabled":false,"hosts":[{"host":"extended-services.{{ .Values.global.kubernetesClusterDomain }}","paths":[{"path":"/","pathType":"ImplementationSpecific"}]}],"tls":[]}` | Extended Services Ingress configuration (https://kubernetes.io/docs/concepts/services-networking/ingress/)                                      |
| extended_services.name             | string | `"extended-services"`                                                                                                                                                                                                                                  | The Extended Services application name                                                                                                          |
| extended_services.nameOverride     | string | `""`                                                                                                                                                                                                                                                   | Overrides charts name                                                                                                                           |
| extended_services.openshiftRoute   | object | `{"annotations":{},"enabled":false,"host":"extended-services.{{ .Values.global.openshiftRouteDomain }}","tls":{"insecureEdgeTerminationPolicy":"None","termination":"edge"}}`                                                                          | Extended Services OpenShift Route configuration (https://docs.openshift.com/container-platform/4.14/networking/routes/route-configuration.html) |
| extended_services.service          | object | `{"nodePort":"","port":21345,"type":"ClusterIP"}`                                                                                                                                                                                                      | Extended Services Service configuration (https://kubernetes.io/docs/concepts/services-networking/service/)                                      |
| extended_services.serviceAccount   | object | `{"annotations":{},"create":true,"name":""}`                                                                                                                                                                                                           | Extended Services ServiceAccount configuration (https://kubernetes.io/docs/concepts/security/service-accounts/)                                 |
| kie_sandbox.autoscaling            | object | `{"enabled":false,"maxReplicas":100,"minReplicas":1,"targetCPUUtilizationPercentage":80}`                                                                                                                                                              | KIE Sandbox HorizontalPodAutoscaler configuration (https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)                  |
| kie_sandbox.env                    | list   | `[{"name":"KIE_SANDBOX_EXTENDED_SERVICES_URL","value":"http://127.0.0.1:21345"},{"name":"KIE_SANDBOX_CORS_PROXY_URL","value":"http://127.0.0.1:8081"}]`                                                                                                | Env variables for KIE Sandbox deployment                                                                                                        |
| kie_sandbox.fullnameOverride       | string | `""`                                                                                                                                                                                                                                                   | Overrides charts full name                                                                                                                      |
| kie_sandbox.image                  | object | `{"account":"kie-tools","name":"kie-sandbox-image","pullPolicy":"IfNotPresent","registry":"quay.io","tag":"latest"}`                                                                                                                                   | Image source configuration for the KIE Sandbox image                                                                                            |
| kie_sandbox.imagePullSecrets       | list   | `[]`                                                                                                                                                                                                                                                   | Pull secrets used when pulling KIE Sandbox image                                                                                                |
| kie_sandbox.ingress                | object | `{"annotations":{},"className":"{{ .Values.global.kubernetesIngressClass }}","enabled":false,"hosts":[{"host":"kie-sandbox.{{ .Values.global.kubernetesClusterDomain }}","paths":[{"path":"/","pathType":"ImplementationSpecific"}]}],"tls":[]}`       | KIE Sandbox Ingress configuration (https://kubernetes.io/docs/concepts/services-networking/ingress/)                                            |
| kie_sandbox.name                   | string | `"kie-sandbox"`                                                                                                                                                                                                                                        | The KIE Sandbox application name                                                                                                                |
| kie_sandbox.nameOverride           | string | `""`                                                                                                                                                                                                                                                   | Overrides charts name                                                                                                                           |
| kie_sandbox.openshiftRoute         | object | `{"annotations":{},"enabled":false,"host":"kie-sandbox.{{ .Values.global.openshiftRouteDomain }}","tls":{"insecureEdgeTerminationPolicy":"None","termination":"edge"}}`                                                                                | KIE Sandbox OpenShift Route configuration (https://docs.openshift.com/container-platform/4.14/networking/routes/route-configuration.html)       |
| kie_sandbox.service                | object | `{"nodePort":"","port":8080,"type":"ClusterIP"}`                                                                                                                                                                                                       | KIE Sandbox Service configuration (https://kubernetes.io/docs/concepts/services-networking/service/)                                            |
| kie_sandbox.serviceAccount         | object | `{"annotations":{},"create":true,"name":""}`                                                                                                                                                                                                           | KIE Sandbox ServiceAccount configuration (https://kubernetes.io/docs/concepts/security/service-accounts/)                                       |

---

Autogenerated from chart metadata using [helm-docs v1.12.0](https://github.com/norwoodj/helm-docs/releases/v1.12.0)
