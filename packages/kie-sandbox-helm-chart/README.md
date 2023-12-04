# KIE Sandbox Helm Chart

This chart can be used to deploy KIE Sandbox image on a [Kubernetes](https://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Additional requirements

- Podman (for Linux)
- Docker (for macOS)
- Minikube

## Components

KIE Sandbox has 3 components: 
- Sandbox: main application
- Extended Services: powers the DMN Runner and Dev deployments features
- Cors Proxy: intended to be used to solve CORS issues

## Installing the Chart

To install the chart with the release name `kie-sandbox`:

```console
$ helm install kie-sandbox .
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

1. Run the following commands in a separate terminal to port-forward Cors Proxy component:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kie-sandbox-helm-chart,app.kubernetes.io/component=cors-proxy,app.kubernetes.io/instance=kie-sandbox" -o jsonpath="{.items[0].metadata.name}")
  export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
  echo "Cors Proxy URL: http://127.0.0.1:8081"
  kubectl --namespace default port-forward $POD_NAME 8081:$CONTAINER_PORT

2. Run the following commands in a separate terminal to port-forward Extendend Services component:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kie-sandbox-helm-chart,app.kubernetes.io/component=extended-services,app.kubernetes.io/instance=kie-sandbox" -o jsonpath="{.items[0].metadata.name}")
  export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
  echo "Extended Services URL: http://127.0.0.1:8081"
  kubectl --namespace default port-forward $POD_NAME 21345:$CONTAINER_PORT

3. Run the following commands in a separate terminal to port-forward Sanxbox component and get the application URL:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kie-sandbox-helm-chart,app.kubernetes.io/component=sandbox,app.kubernetes.io/instance=kie-sandbox" -o jsonpath="{.items[0].metadata.name}")
  export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
  echo "KIE Sandbox URL http://127.0.0.1:8080"
  kubectl --namespace default port-forward $POD_NAME 8080:$CONTAINER_PORT
```

Run above commands to forward container ports to your system ports. After this, KIE Sandbox should be accessible via http://127.0.0.1:8080

## Uninstalling the Chart

To uninstall the `kie-sandbox` deployment:

```console
$ helm uninstall kie-sandbox
```

## Passing Environmental variables

This chart uses default environmental variables from `values.yaml` file. We can override those by passing it from command line.

```console
$ helm install kie-sandbox ./ --set image.repository=quay.io
```

## Configuration

The following table lists the configurable parameters of the KIE Sandbox chart and their default values.

| Parameter            | Description                                                            | Default               |
| -------------------- | ---------------------------------------------------------------------- | --------------------- |
| `image.registry`     | Name of image registry                                                 | `"quay.io"`           |
| `image.account`      | Account of image                                                       | `"kie-tools"`         |
| `image.tag`          | Overrides the KIE Sandbox image tag whose default is the chart version | `"latest"`            |
| `image.name`         | The name of image                                                      | `"kie-sandbox-image"` |
| `extendedServiceUrl` | The Url of running extended service container                          | `""`                  |
| `corsProxyUrl`       | The Url of running Cors Proxy container                                | `""`                  |
| `service.port`       | The http service port port                                             | `8080`                |
