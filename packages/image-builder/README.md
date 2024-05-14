```
@kie-tools/image-builder [command]

Commands:
  @kie-tools/image-builder build      Builds the image locally and store it in your local Docker/Podman image registry
  @kie-tools/image-builder minikube   Builds the image locally and load it to your Minikube cluster
  @kie-tools/image-builder kind       Builds the image locally and load it to your Kind cluster
  @kie-tools/image-builder openshift  Builds the image on the OpenShift cluster in an ImageStream

Options:
  -r, --registry       The string for the image registry  [string]
  -a, --account        The string for the image account  [string]
  -n, --name           The string for the image name  [string] [required]
  -t, --tags           The string for the image tags  [array] [required]
  -e, --engine         The build engine to be used  [string] [choices: "docker", "podman"] [default: "docker"]
  -p, --push           Push the image to the registry  [boolean] [default: false]
  -f, --containerfile  Path to the Containerfile/Dockerfile  [string] [default: "Containerfile"]
  -c, --context        Path to the build context  [string] [default: "./"]
      --build-arg      Build args for the builder in the format '<arg>=<value>', where <value> is a string (Can be used multiple times)  [array] [default: []]
      --arch           The target build architecture. If not provided will default to the native architecture  [string] [choices: "amd64", "arm64", "native"] [default: "native"]
  -h, --help           Show help  [boolean]

Examples:
  $ image-builder --registry "$(build-env myCustomEnv.registry)" --account "$(build-env myCustomEnv.account)" --name "$(build-env myCustomEnv.name)" --tags "$(build-env myCustomEnv.buildTags)" --engine docker --push  Build an image using parameters from your myCustomEnv build env variables


 CLI tool to help building container images using build variables and different engines on different OSes.
 Also useful to aid on developing images and pushing them to Kubernetes/OpenShift clusters.
```
