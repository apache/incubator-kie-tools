## K8s YAML to APIServer Requests

Library to map Kubernetes API resources and apply resource YAMLs

## Features

- interpolateK8sResourceYamls(): Given an YAML and a tokens map, interpolate the YAML content with values provided by the token map. Tokens are represented with `${{token.path.name}}`.
- parseK8sResourceYaml(): Given a YAML file, return an array of JSON objects parsed.
- buildK8sApiServerEndpointsByResourceKind(): Generates a map of Kubernetes resources and their API endpoints.
- callK8sApiServer(): Make the requests to the Kubernetes APIServer in order to create given resources.

## How to use

Import as a library and invoke the functions described above.

## How to test

Run

```bash
pnpm start <k8sApiServerUrl> <k8sNamespace> <k8sServiceAccountToken> <k8sYamlFilepath>
```

to test changes made to this libray.

#### Example:

```bash
pnpm start https://api.to.my.openshift.cluster.com:6443 my-project sha256~MGnPXMPsi1YJkCV6kr970gQYI6KtQWztIObm3jQxUJI ../myDeployment.yaml
```
