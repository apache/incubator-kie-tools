# We love contributions!

- [We love contributions!](#we-love-contributions)
    - [How can I contribute?](#how-can-i-contribute)
    - [Contributing to the SonataFlow Operator codebase](#contributing-to-the-sonataflow-operator-codebase)
    - [Contributing to the SonataFlow Operator](#contributing-to-the-sonataflow-operator)
        - [Prerequisites](#prerequisites)
        - [Getting Started](#getting-started)
        - [Test It Out locally](#test-it-out-locally)
        - [How-tos](#how-tos)
            - [Modifying the API definitions](#modifying-the-api-definitions)
            - [Building](#building)
            - [Deploy](#deploy)
            - [Undeploy](#undeploy)
        - [Running the operator on the cluster](#running-the-operator-on-the-cluster)
        - [Configuration](#configuration)
    - [Customize Builder Image](#customize-builder-image)
- [Development status](#development-status)
    - [General notes](#general-notes)
        - [Workflow CR](#workflow-cr)
        - [Platform CR](#platform-cr)
    - [Improvements](#improvements)
- [Tekton Pipeline to build and deploy the Operator](#tekton-pipeline-to-build-and-deploy-the-operator)

## How can I contribute?

There are many ways you can contribute to SonataFlow Operator, not only software development, as well as
with the rest of Kogito community:

- Contribute actively to development (see the section below)
- Use it and report any feedback, improvement or bug you may find via Github, mailing list or chat.
- Contribute by writing missing documentation or blog posts about the features around Kogito
- Tweet, like and socialize Kogito in your preferred social network
- Enjoy the talks that the contributors submit in various conferences around the world

## Contributing to the SonataFlow Operator codebase

The main project is written in go.
SonataFlow Operator is built on top of Kubernetes through Custom Resource Definitions.

- Workflow
- Platform
- Build

This project aims to follow the
Kubernetes [Operator pattern](https://kubernetes.io/docs/concepts/extend-kubernetes/operator/)

It uses [Controllers](https://kubernetes.io/docs/concepts/architecture/controller/)
which provides a reconcile function responsible for synchronizing resources untile the desired state is reached on the
cluster

## Contributing to the SonataFlow Operator

### Prerequisites

The Operator's controllers and the configurations are generated using the Operator sdk, the tasks are executed using a
Makefile. 

More information about annotations can be found via
the [Kubebuilder Documentation](https://book.kubebuilder.io/introduction.html)

In order to build the project, you need to comply with the following requirements:

- [operator-sdk-v1.25.0+](https://sdk.operatorframework.io/docs/building-operators/golang/installation/)
- [Go 1.21+](https://go.dev/dl/)
- [Kubebuilder 3.7.0+](https://github.com/kubernetes-sigs/kubebuilder/releases)
- [CEKit 4.8.0+](https://cekit.io/)

GNU Make:
Used to define composite build actions. This should be already installed or available as a
package (https://www.gnu.org/software/make/).

> **NOTE:** Run `make help` for more information on all potential `make` targets

### Getting Started

You’ll need a Kubernetes cluster to run against. You can use:

- [KIND](https://sigs.k8s.io/kind)
- [MINIKUBE](https://minikube.sigs.k8s.io)
- [Openshift Local](https://console.redhat.com/openshift/create/local)
- [Openshift-developer-sandbox-trial](https://www.redhat.com/en/technologies/cloud-computing/openshift/openshift-developer-sandbox-trial)

> **NOTE:** Your controller will automatically use the current context in your kubeconfig file (i.e. whatever
> cluster `kubectl cluster-info` shows).

> **IMPORTANT**: Please make sure that your [kubectl](https://kubernetes.io/docs/tasks/tools/) is version 1.24.0 or
> later
> since there's a bug performing validation on default attributes in Custom Resources.

### Test It Out locally

You can launch the operator locally and bind to your cluster.

1. Install the CRDs into the cluster:

```sh
make install
```

2. Run your controller (this will run in the foreground, so switch to a new terminal if you want to leave it running):

```sh Kubernetes cluster to run against. You can use:
make run
```

> **NOTE:** You can also run this in one step by running: `make install run`

> **NOTE:** Run `make help` for more information on all potential `make` targets

More information can be found via the [Kubebuilder Documentation](https://book.kubebuilder.io/introduction.html)

### How-tos

#### Modifying the API definitions

If you are editing the API definitions, generate the manifests such as CRs or CRDs using:

```sh
make manifests
```

#### Building

```sh
make container-build
```

#### Deploy

```sh
make deploy
```

#### Undeploy

```sh
make undeploy
```

### Change log level
By Default the log level is set to show only ERRORS with 
```sh
- "--v=0" 
```
inside the manager_auth_proxy_patch.yaml in the containers' section of kube-rbac-proxy and manager.

With the 
```sh
make generate-all 
```
whese values will be replicated on operator.yaml and on sonataflow-operator.clusterserviceversion.yaml containers' sections.

If you want to see the INFO msg replace v=0 with v=2 in the files during the development or in the deployment files on the cluster

The available levels are:
- v=0 > Error
- v=1 > Warning
- v=2 >Info
- v=3 > Debug

### Running the operator on the cluster

See the section on [README](../README.md#getting-started)

### Configuration

A configmap called `sonataflow-operator-builder-config` will be created under the `sonataflow-operator-system` namespace
when the Operator will be installed, and it contains:

- DEFAULT_BUILDER_RESOURCE = Dockerfile
- DEFAULT_WORKFLOW_EXTENSION = .sw.json
- Dockerfile = `<dockerfile content>`

## Customize Builder Image

At the startup a [Dockerfile](../config/manager/sonataflow_builder_dockerfile.yaml) is placed in a configmap. This
Dockerfile uses a base image
called [kogito-swf-builder](https://github.com/kiegroup/kogito-images/tree/master/modules/kogito-swf-builder) with:

- openjdk 11+
- maven 3.8.6+
- a Quarkus project  `/home/kogito/serverless-workflow-project` with those extensions:
    - quarkus-kubernetes
    - kogito-quarkus-serverless-workflow
    - kogito-addons-quarkus-knative-eventing
- all the dependencies of Quarkus and the extensions stored in the `/home/kogito/.m2` directory in the image.

There are, in the base image, some additional scripts in case of need to apply changes like this:

- add other quarkus extensions in `/home/kogito/launch/add-extension.sh`
- build the project after adding other files/java classes in `/home/kogito/launch/build-app.sh`
- create a new project in `/home/kogito/launch/create-app.sh`

You can customize your final Image changing the Dockerfile in the configmap sonataflow-operator-builder-config
accordingly to your specific needs.

# Development status

## General notes

### Workflow CR

- At the moment we are supporting only deployment of services on Kubernetes

### Platform CR

- The only tested features are the ones related to the docker Registry customization and so:

```
       apiVersion: sonataflow.org/v1alpha08
        kind: SonataFlowPlatform
        metadata:
            name: greeting-workflow-platform
        spec:
            platform:
                registry:
                    address: <docker registry repository> // the URI to access
                    secret: <name of the secret> // the secret where credentials are stored
                    insecure: true // if the container registry is insecure (ie, http only)
                    ca: <name of the config map> // the configmap which stores the Certificate Authority
                    organization: <name of the org> // the registry organization
```

## Improvements

- Introduce actions into Workflow and Build controller to improve code clarity
- Add Trait to the Platform CR in order to be able to deploy on different context (i.e. KNative)
- Test the Kaniko cache feature
- Improve the workflow converters in order to support all the SonataFlow Workflow features

# Tekton Pipeline to build and deploy the Operator

Setup a [pipeline](docs/PIPELINE.md) on a Openshift cluster.