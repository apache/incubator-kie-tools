# kn-plugin-workflow

`kn-plugin-workflow` is a plugin of the Knative Client, to enable users to quickly set up a local Kogito Serverless Workflow project from the command line.

## Getting Started

Note: In order to use the `workflow` plugin, you must install:

- [Java](https://www.java.com/en/download/help/download_options.html) at least version 11
- [Maven](https://maven.apache.org/install.html) at least version 3.8.1
- [Docker](https://docs.docker.com/engine/install/) (Optional)
- [Podman](https://docs.podman.io/en/latest/) (Optional)
- [Kubernetes CLI `kubectl`](https://kubernetes.io/docs/tasks/tools/install-kubectl).

### Instalation

You can download the latest binaries from the [Releases](https://github.com/kiegroup/kie-tools/releases) page.

There are two ways to run `kn workflow`:

1. You can run it standalone, just put it on your system path and make sure it is executable.
2. You can install it as a plugin of the `kn` client to run:
   - Follow the [documentation](https://github.com/knative/client/blob/main/docs/README.md#installing-kn) to install `kn` CLI if you don't have it
   - Copy the `kn-workflow` binary to a directory on your `PATH` (for example, `/usr/local/bin`) and make sure its filename is `kn-workflow`
     - On macOS give the ownership to the root user: `sudo chown root: /usr/local/bin/kn-workflow`
   - Run `kn plugin list` to verify that the `kn-workflow` plugin is installed successfully

After the plugin is installed, you can use `kn workflow` to run its related subcommands.

## Usage

```
Get up and running with a local Knative environment

Usage:
  kn workflow [command]

Available Commands:
  build       Build a Kogito Serverless Workflow project and generate a container image
  completion  Generate the autocompletion script for the specified shell
  create      Create a Kogito Serverless Workflow project
  deploy      Deploy a Kogito Serverless Workflow project
  help        Help about any command

Flags:
  -h, --help      help for kn-workflow
  -v, --verbose   Print verbose logs

Use "kn workflow [command] --help" for more information about a command.
```

### create

This command will scaffold a new Kogito Serverless Workflow project named "my-project":

```bash
kn workflow create --name my-project
```

### build

Builds a Kogito Serverless Workflow project in the current directory and generate a specific image:

```bash
kn workflow build --image quay.io/mysuer/myproject
```

### deploy

Deploys a Kogito Serverless Workflow project in the current directory (build command is required):

```bash
kn workflow deploy
```

## Building from Source

```bash
git clone git@github.com:kie-group/kie-tools.git
cd kie-tools
yarn bootstrap
cd packages/kn-plugin-workflow
go mod tidy
yarn build
```
