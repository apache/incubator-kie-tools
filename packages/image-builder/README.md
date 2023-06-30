```
Options:
  -r, --registry       The build-env path or string for the image registry  [string]
  -a, --account        The build-env path or string for the image account  [string]
  -n, --name           The build-env path or string for the image name  [string] [required]
  -t, --tags           The build-env path or string for the image tags  [string] [required]
  -e, --engine         The build engine to be used  [string] [choices: "docker", "podman"] [default: "docker"]
  -p, --push           Push the image to the registry  [boolean] [required] [default: false]
  -f, --containerfile  Path to the Containerfile/Dockerfile  [string] [required] [default: "Containerfile"]
  -c, --context        Path to the build context  [string] [required] [default: "./"]
      --build-arg      Build args for the builder in the format '<arg>=<value>', where <value> can be a string or a build-env path (Can be used multiple times)  [array] [default: []]
  -h, --help           Show help  [boolean]
```
