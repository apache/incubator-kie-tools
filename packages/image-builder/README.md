```
Options:
  -r, --registry       The string for the image registry  [string]
  -a, --account        The string for the image account  [string]
  -n, --name           The string for the image name  [string] [required]
  -t, --tags           The string for the image tags  [string] [required]
  -e, --engine         The build engine to be used  [string] [choices: "docker", "podman"] [default: "docker"]
  -p, --push           Push the image to the registry  [boolean] [required] [default: false]
  -f, --containerfile  Path to the Containerfile/Dockerfile  [string] [required] [default: "Containerfile"]
  -c, --context        Path to the build context  [string] [required] [default: "./"]
      --build-arg      Build args for the builder in the format '<arg>=<value>', where <value> is a string (Can be used multiple times)  [array] [default: []]
  -h, --help           Show help  [boolean]

Examples:
  $ image-builder --registry "$(build-env myCustomEnv.registry)" --account "$(build-env myCustomEnv.account)" --name "$(build-env myCustomEnv.name)" --tags "$(build-env myCustomEnv.buildTags)" --engine docker --push  Build an image using parameters from your myCustomEnv build env variables


 CLI tool to help building container images using build variables and different engines on different OSes.
```
