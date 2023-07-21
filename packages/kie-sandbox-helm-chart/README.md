# KIE Sandbox Helm Chart

This chart can be used to deploy KIE Sandbox image on a [Kubernetes](https://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Additional requirements

- Podman (for Linux)
- Docker (for macOS)
- Minikube

## Installing the Chart

To install the chart with the release name `kie-sandbox`:

First download all dependent chart into the the charts folder using below command.

```console
$ helm dependency update
```

then run the helm chart using command

```console
$ helm install kie-sandbox ./ --set extendedServiceUrl=http://127.0.0.1:21345 --set gitCorsProxyUrl=http://127.0.0.1:8081
```

Following message should be displayed on your console.

```console
NAME: kie-sandbox-helm-chart
LAST DEPLOYED: Wed Jun 14 15:45:09 2023
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
1. Get the application URL by running these commands:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=chalja,app.kubernetes.io/instance=chalja" -o jsonpath="{.items[0].metadata.name}")
  export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
  echo "Visit http://127.0.0.1:8080 to use your application"
  kubectl --namespace default port-forward $POD_NAME 8080:$CONTAINER_PORT
```

Run above commands to forward container port to your system port 8080. After this, KIE Sandbox should be accessible via http://127.0.0.1:8080

We also need to forward port of KIE extended services and Git cors proxy container. Run following commands

```console
$ kubectl get pods
```

Use the $POD_NAME of KIE extended services as in below command

```console
$ kubectl --namespace default port-forward $POD_NAME 21345:21345
```

Similarly, Use the $POD_NAME of git cors proxy as in below command

```console
$ kubectl --namespace default port-forward $POD_NAME 8081:8080
```

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
| `gitCorsProxyUrl`    | The Url of running git cors proxy container                            | `""`                  |
| `service.port`       | The http service port port                                             | `8080`                |
