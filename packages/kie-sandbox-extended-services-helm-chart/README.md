# KIE Sandbox Extended Services Helm Chart

This chart can be used to deploy KIE Sandbox extended service image on a [Kubernetes](https://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Installing the Chart

To install the chart with the release name `kie-sandbox-extended-services`:

```console
$ helm install kie-sandbox-extended-services ./src
```

## Uninstalling the Chart

To uninstall the `kie-sandbox-extended-services` deployment:

```console
$ helm uninstall kie-sandbox-extended-services
```

## Passing Environmental variables

This chart uses default environmental variables from `values.yaml` file. We can override those by passing it from command line.

```console
$ helm install kie-sandbox-extended-services ./src --set image.repository=quay.io
```

## Configuration

The following table lists the configurable parameters of the KIE Sandbox extended services chart and their default values.

| Parameter        | Description                                                                              | Default                                 |
| ---------------- | ---------------------------------------------------------------------------------------- | --------------------------------------- |
| `image.registry` | Name of image registry                                                                   | `"quay.io"`                             |
| `image.account`  | Account of image                                                                         | `"kie-tools"`                           |
| `image.tag`      | Overrides the KIE Sandbox extended services image tag whose default is the chart version | `"latest"`                              |
| `image.name`     | The name of image                                                                        | `"kie-sandbox-extended-services-image"` |
| `service.port`   | The http service port port                                                               | `21345`                                 |
