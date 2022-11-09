# We love contributions!

How can I contribute?

There are many ways you can contribute to Kogito Serverless Workflow Operator, not only software development, as well as with the rest of Kogito community:

- Contribute actively to development (see the section below)
- Use it and report any feedback, improvement or bug you may find via Github, mailing list or chat.
- Contribute by writing missing documentation or blog posts about the features around Kogito
- Tweet, like and socialize Kogito in your preferred social network
- Enjoy the talks that the contributors submit in various conferences around the world

# Contributing to the Kogito Serverless Operator codebase

The main project is written in go. 
Kogito Serverless Workflow Operator is built on top of Kubernetes through Custom Resource Definitions.

- Workflow
- Platform
- Build

This project aims to follow the Kubernetes [Operator pattern](https://kubernetes.io/docs/concepts/extend-kubernetes/operator/)

It uses [Controllers](https://kubernetes.io/docs/concepts/architecture/controller/)
which provides a reconcile function responsible for synchronizing resources untile the desired state is reached on the cluster


# Contributing to the Kogito Serverless Operator

# PreRequisites
The Operator's controllers and the configurations are generated using the Operator sdk, the tasks are executed using a Makefile
More information about annotations can be found via the [Kubebuilder Documentation](https://book.kubebuilder.io/introduction.html)

In order to build the project, you need to comply with the following requirements:

- [operator-sdk-v1.25.0+](https://sdk.operatorframework.io/docs/building-operators/golang/installation/)
- [Go 1.19+](https://go.dev/dl/)
- [Kubebuilder 3.7.0+](https://github.com/kubernetes-sigs/kubebuilder/releases)


GNU Make: 
used to define composite build actions. This should be already installed or available as a package (https://www.gnu.org/software/make/).

**NOTE:** Run `make help` for more information on all potential `make` targets

## Getting Started
You’ll need a Kubernetes cluster to run against. You can use 
- [KIND](https://sigs.k8s.io/kind) 
- [MINIKUBE](https://minikube.sigs.k8s.io)  
- [Openshift Local](https://console.redhat.com/openshift/create/local) 
- [Openshift-developer-sandbox-trial](https://www.redhat.com/en/technologies/cloud-computing/openshift/openshift-developer-sandbox-trial)

**Note:** Your controller will automatically use the current context in your kubeconfig file (i.e. whatever cluster `kubectl cluster-info` shows).

### Modifying the API definitions
If you are editing the API definitions, generate the manifests such as CRs or CRDs using:
```sh
make manifests
```

### Building
```sh
make container-build
```

### Deploy
```sh
make deploy
```

### Undeploy
```sh
make deploy
```

### Running on the cluster
See the section on [README](./README.md)
About mandatory namespaces and secret


### Configuration

A configmap called kogito-serverless-operator-builder-config will be created under the kogito-builder namespace when the Operator will be installed, and it contains:

- DEFAULT_BUILDER_RESOURCE = kogito_builder_dockerfile.yaml
- DEFAULT_WORKFLOW_DEXTENSION = .sw.json
- DEFAULT_KANIKO_SECRET_DEFAULT = regcred
- DEFAULT_REGISTRY_REPO = quay.io/kiegroup
- kogito_builder_dockerfile.yaml = <dockerfile content>

For the local development the DEFAULT_REGISTRY_REPO must be changed, this operation needs to be performed immediately 
after the operator deploy (before the first reconcile of the Workflow CR), otherwise to take effects a restart of the 
operator needs to be performed to have this change applied.

# Customize Builder Image
At the startup a [Dockerfile](./config/manager/kogito_builder_dockerfile.yaml) is placed in a configmap, this Dockerfile use a base image called [swfbuilder](https://github.com/kiegroup/kogito-images/tree/master/modules/kogito-swf-builder) with:

- openjdk 11
- maven 3.8.6

A Quarkus project  `/home/kogito/serverless-workflow-project` with the extensions
- quarkus-kubernetes 
- kogito-quarkus-serverless-workflow 
- kogito-addons-quarkus-knative-eventing

All the dependencies of quarkus and the extensions are store in the `/home/kogito/.m2` directory. additional scripts in case of need to apply changes like this: 

- add other quarkus extensions,
- build the project after adding other files/java classes
- create a new project

You can customize your final Image changing the Dockerfile in the configmap