# Git Cors Proxy Helm Chart

This chart can be used to deploy Git CORS Proxy on a [Kubernetes](https://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Installing the Chart

To install the chart with the release name `git-cors-proxy`:

```console
$ helm install git-cors-proxy ./src
```

## Uninstalling the Chart

To uninstall the `git-cors-proxy` deployment:

```console
$ helm uninstall git-cors-proxy
```

## Passing Environmental variables

This chart uses default environmental variables from `values.yaml` file. We can override those by passing it from command line.

```console
$ helm install git-cors-proxy ./src --set image.repository=quay.io
```

## Configuration

The following table lists the configurable parameters of the git cors proxy chart and their default values.

| Parameter        | Description                                                               | Default                  |
| ---------------- | ------------------------------------------------------------------------- | ------------------------ |
| `image.registry` | Name of image registry                                                    | `"quay.io"`              |
| `image.account`  | Account of image                                                          | `"kie-tools"`            |
| `image.tag`      | Overrides the git cors proxy image tag whose default is the chart version | `"latest"`               |
| `image.name`     | The name of image                                                         | `"git-cors-proxy-image"` |
| `service.port`   | The http service port port                                                | `8080`                   |
