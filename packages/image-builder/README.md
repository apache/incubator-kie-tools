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

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may be missing the ASF Licensing Header
-

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html
